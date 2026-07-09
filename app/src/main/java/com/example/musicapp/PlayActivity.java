package com.example.musicapp;

import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicapp.model.Song;
import com.example.musicapp.view.LyricView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlayActivity extends AppCompatActivity {

    // 控件
    private ImageView ivBack;
    private ImageView ivCover;
    private TextView tvSongName;
    private TextView tvArtist;
    private TextView tvCurrent;
    private TextView tvTotal;
    private SeekBar seekBar;
    private ImageButton btnPrev, btnPlay, btnNext;
    private LyricView lyricView;

    // 数据
    private List<Song> songs = new ArrayList<>();
    private int currentIndex = 0;

    // 播放器
    private MediaPlayer player;
    private boolean isPrepared = false;     // 是否已经 prepare 完成
    private boolean isUserSeeking = false;  // 用户是否正在拖动 SeekBar

    // 主线程 Handler，用于刷新进度 + 歌词
    private final Handler handler = new Handler(Looper.getMainLooper());

    // 封面旋转动画
    private ObjectAnimator coverAnimator;
    // 暂停时记录当前旋转角度，以便恢复时接着转
    private float coverAngle = 0f;
    // 进度刷新任务
    private final Runnable updateProgressTask = new Runnable() {
        @Override
        public void run() {
            if (player != null && isPrepared && !isUserSeeking) {
                int curMs = player.getCurrentPosition();
                int totalMs = player.getDuration();
                // 更新进度条（max 设为 1000 防止 long 溢出）
                if (totalMs > 0) {
                    int progress = (int) (curMs * 1000L / totalMs);
                    seekBar.setProgress(progress);
                }
                tvCurrent.setText(formatTime(curMs));
                // 同步歌词高亮 + 滚动
                lyricView.updateTime(curMs);
            }
            // 每 200ms 刷新一次，足够流畅
            handler.postDelayed(this, 200);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        bindViews();
        parseIntent();
        setupListeners();
        playCurrent();
    }

    /** 第 1 步：拿到所有控件引用 */
    private void bindViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        tvSongName = findViewById(R.id.tv_song_name);
        tvArtist = findViewById(R.id.tv_artist);
        tvCurrent = findViewById(R.id.tv_current);
        tvTotal = findViewById(R.id.tv_total);
        seekBar = findViewById(R.id.seek_bar);
        btnPrev = findViewById(R.id.btn_prev);
        btnPlay = findViewById(R.id.btn_play);
        btnNext = findViewById(R.id.btn_next);
        lyricView = findViewById(R.id.lyric_view);
    }

    /** 第 2 步：从 Intent 取出歌曲列表 + 当前索引 */
    private void parseIntent() {
        String json = getIntent().getStringExtra("songs");
        currentIndex = getIntent().getIntExtra("index", 0);

        if (json != null) {
            // Gson 反序列化 List<Song>
            List<Song> list = new Gson().fromJson(json,
                    new TypeToken<List<Song>>() {}.getType());
            if (list != null) {
                songs = list;
            }
        }
        if (currentIndex < 0 || currentIndex >= songs.size()) {
            currentIndex = 0;
        }
    }

    /** 第 3 步：注册各种按钮事件 */
    private void setupListeners() {
        // 返回
        ivBack.setOnClickListener(v -> finish());

        // 播放 / 暂停
        btnPlay.setOnClickListener(v -> togglePlay());

        // 上一首
        btnPrev.setOnClickListener(v -> {
            if (songs.isEmpty()) return;
            currentIndex = (currentIndex - 1 + songs.size()) % songs.size();
            playCurrent();
        });

        // 下一首
        btnNext.setOnClickListener(v -> {
            if (songs.isEmpty()) return;
            currentIndex = (currentIndex + 1) % songs.size();
            playCurrent();
        });

        // SeekBar 拖动
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && player != null && isPrepared) {
                    int total = player.getDuration();
                    int target = (int) (progress * total / 1000L);
                    tvCurrent.setText(formatTime(target));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null && isPrepared) {
                    int total = player.getDuration();
                    int target = (int) (seekBar.getProgress() * total / 1000L);
                    player.seekTo(target);
                    // 跳转后立刻刷新歌词位置
                    lyricView.updateTime(target);
                }
                isUserSeeking = false;
            }
        });
    }

    /** 切换播放/暂停状态 */
    private void togglePlay() {
        if (player == null || !isPrepared) return;
        if (player.isPlaying()) {
            player.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
            pauseCoverRotation();
        } else {
            player.start();
            btnPlay.setImageResource(R.drawable.ic_pause);
            resumeCoverRotation();
        }
    }


    private void playCurrent() {
        if (songs.isEmpty()) {
            Toast.makeText(this, "没有可播放的歌曲", Toast.LENGTH_SHORT).show();
            return;
        }
        Song song = songs.get(currentIndex);

        // 更新 UI
        tvSongName.setText(song.name == null ? "未知" : song.name);
        tvArtist.setText(song.getArtistNames());
        tvCurrent.setText("00:00");
        tvTotal.setText(formatTime(song.duration * 1000L));
        seekBar.setProgress(0);

        // 加载封面（圆形裁剪）
        String coverUrl = (song.cover != null) ? song.cover.large : null;
        if (coverUrl != null) {
            Glide.with(this)
                    .load(coverUrl)
                    .apply(RequestOptions.circleCropTransform()) // 圆形
                    .placeholder(R.drawable.bg_cover_placeholder)
                    .error(R.drawable.bg_cover_placeholder)
                    .into(ivCover);
        } else {
            ivCover.setImageResource(R.drawable.bg_cover_placeholder);
        }

        // 加载歌词
        String lrcText = (song.lyric != null) ? song.lyric.text : null;
        lyricView.setLyric(lrcText);

        // 准备 & 播放
        if (song.url == null || song.url.isEmpty()) {
            Toast.makeText(this, "该歌曲没有可用的播放链接", Toast.LENGTH_SHORT).show();
            return;
        }
        preparePlayer(song.url);
    }

    /**
     * 准备 MediaPlayer 播放 url
     */
    private void preparePlayer(String url) {
        // 释放上一次的实例
        releasePlayer();

        isPrepared = false;
        btnPlay.setImageResource(R.drawable.ic_play);

        player = new MediaPlayer();
        try {
            player.setDataSource(url);
            // 准备完成回调
            player.setOnPreparedListener(mp -> {
                isPrepared = true;
                tvTotal.setText(formatTime(mp.getDuration()));
                mp.start();
                btnPlay.setImageResource(R.drawable.ic_pause);
                // 启动定时刷新 + 封面旋转
                handler.removeCallbacks(updateProgressTask);
                handler.post(updateProgressTask);
                startCoverRotation();
            });
            // 播放完成回调 -> 自动下一首
            player.setOnCompletionListener(mp -> {
                if (songs.isEmpty()) return;
                currentIndex = (currentIndex + 1) % songs.size();
                playCurrent();
            });
            // 错误回调
            player.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(PlayActivity.this,
                        "播放出错（" + what + "），尝试下一首", Toast.LENGTH_SHORT).show();
                return false;
            });
            // 异步准备（网络资源不能用同步 prepare，否则会卡 UI）
            player.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "无法播放：" + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** 释放播放器 */
    private void releasePlayer() {
        handler.removeCallbacks(updateProgressTask);
        if (player != null) {
            try {
                if (player.isPlaying()) player.stop();
            } catch (IllegalStateException ignored) {}
            player.release();
            player = null;
        }
        isPrepared = false;
        stopCoverRotation();
    }

    /**
     * 从头开始旋转（切歌时调用）
     */
    private void startCoverRotation() {
        stopCoverRotation();
        coverAngle = 0f;
        ivCover.setRotation(0f);
        coverAnimator = ObjectAnimator.ofFloat(
                ivCover, "rotation", 0f, 360f);
        coverAnimator.setDuration(20_000);          // 20 秒转一圈
        coverAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        coverAnimator.setInterpolator(new LinearInterpolator());
        coverAnimator.start();
    }

    /**
     * 从暂停位置恢复旋转
     */
    private void resumeCoverRotation() {
        if (coverAnimator != null && coverAnimator.isPaused()) {
            coverAnimator.resume();
            return;
        }
        // 如果动画已结束/不存在，新建一个从当前位置开始转
        stopCoverRotation();
        float start = ivCover.getRotation();
        coverAnimator = ObjectAnimator.ofFloat(
                ivCover, "rotation", start, start + 360f);
        coverAnimator.setDuration(20_000);
        coverAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        coverAnimator.setInterpolator(new LinearInterpolator());
        coverAnimator.start();
    }

    /**
     * 暂停旋转（播放暂停时调用）
     */
    private void pauseCoverRotation() {
        if (coverAnimator != null && coverAnimator.isRunning()) {
            coverAnimator.pause();
            coverAngle = ivCover.getRotation();
        }
    }

    /**
     * 完全停止旋转并释放动画资源
     */
    private void stopCoverRotation() {
        if (coverAnimator != null) {
            coverAnimator.cancel();
            coverAnimator = null;
        }
        coverAngle = 0f;
    }

    /** ms -> "mm:ss" */
    private String formatTime(long ms) {
        if (ms < 0) ms = 0;
        long sec = ms / 1000;
        long m = sec / 60;
        long s = sec % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", m, s);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 页面不可见时暂停播放（用户切到后台）
        if (player != null && player.isPlaying()) {
            player.pause();
            btnPlay.setImageResource(R.drawable.ic_play);
        }
        pauseCoverRotation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity 销毁一定要释放 MediaPlayer，否则会内存泄漏
        releasePlayer();
    }
}
