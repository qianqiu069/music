package com.example.musicapp.utils;

import android.os.Handler;
import android.os.Looper;

import com.example.musicapp.model.Song;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * MusicApi - 音乐 API 调用封装
 */
public class MusicApi {

    private static final String API_KEY =
            "62ccfd8be755cc5850046044c6348d6cac5ef31bd5874c1352287facc06f94c4";
    private static final String BASE_URL = "http://cyapi.top/API/qq_music.php";

    // 全局共享一个 OkHttpClient
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    // 主线程 Handler，用于把网络回调切回 UI 线程
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private static final Gson GSON = new Gson();

    public interface SearchCallback {
        void onSuccess(List<Song> songs);
        void onError(String message);
    }

    public static void search(String keyword, int count, final SearchCallback cb) {
        if (keyword == null || keyword.trim().isEmpty()) {
            cb.onError("关键词不能为空");
            return;
        }
        // 并发收集结果
        final List<Song> songs = new ArrayList<>();
        for (int i = 0; i < count; i++) songs.add(null);

        final AtomicInteger pending = new AtomicInteger(count);
        final boolean[] errorReported = {false};

        for (int i = 1; i <= count; i++) {
            final int index = i;
            okhttp3.HttpUrl url = okhttp3.HttpUrl.parse(BASE_URL).newBuilder()
                    .addQueryParameter("apikey", API_KEY)
                    .addQueryParameter("msg", keyword)
                    .addQueryParameter("num", String.valueOf(count))
                    .addQueryParameter("type", "json")
                    .addQueryParameter("n", String.valueOf(index))
                    .build();

            Request request = new Request.Builder().url(url).get().build();

            CLIENT.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    onOneDone(null);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Song s = null;
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            String body = response.body().string();
                            // API 返回的是单个 song JSON 对象
                            s = GSON.fromJson(body, Song.class);
                        }
                    } catch (Exception ignored) {
                        // JSON 解析失败 -> s 保持 null
                    } finally {
                        response.close();
                    }
                    onOneDone(s);
                }

                private void onOneDone(Song s) {
                    synchronized (songs) {
                        // 按原始顺序放置（index 是从 1 开始）
                        songs.set(index - 1, s);
                    }
                    if (pending.decrementAndGet() == 0) {
                        // 全部完成，过滤 null 后回调
                        final List<Song> valid = new ArrayList<>();
                        for (Song sg : songs) {
                            if (sg != null && sg.name != null) {
                                valid.add(sg);
                            }
                        }
                        MAIN.post(new Runnable() {
                            @Override
                            public void run() {
                                if (valid.isEmpty()) {
                                    if (!errorReported[0]) {
                                        errorReported[0] = true;
                                        cb.onError("没有找到相关歌曲");
                                    }
                                } else {
                                    cb.onSuccess(valid);
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
