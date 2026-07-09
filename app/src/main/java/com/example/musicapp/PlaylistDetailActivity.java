package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.MusicApi;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class PlaylistDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private ImageView ivCover;
    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvAuthor;
    private View llPlayAll;
    private RecyclerView rvSongs;

    private final List<Song> songs = new ArrayList<>();
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        bindViews();
        setupHeader();
        setupRecycler();
        loadSongs();
    }

    private void bindViews() {
        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        tvTitle = findViewById(R.id.tv_title);
        tvDesc = findViewById(R.id.tv_desc);
        tvAuthor = findViewById(R.id.tv_author);
        llPlayAll = findViewById(R.id.ll_play_all);
        rvSongs = findViewById(R.id.rv_songs);

        ivBack.setOnClickListener(v -> finish());
    }

    /**
     * 从 Intent 取出歌单基本信息，填到头部
     */
    private void setupHeader() {
        String title = getIntent().getStringExtra("playlist_title");
        String desc = getIntent().getStringExtra("playlist_desc");
        int coverRes = getIntent().getIntExtra("cover_res", R.drawable.app);

        tvTitle.setText(title == null ? getString(R.string.playlist_detail_title) : title);
        tvDesc.setText(desc == null ? "" : desc);
        tvAuthor.setText(getString(R.string.playlist_author_by, "网易云音乐"));
        ivCover.setImageResource(coverRes);
    }

    /** 初始化 RecyclerView */
    private void setupRecycler() {
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        adapter = new SongAdapter(songs);
        rvSongs.setAdapter(adapter);

        // 点击歌曲 -> 跳到播放页
        adapter.setOnItemClickListener((position, song) -> openPlay(position));

        // 播放全部
        llPlayAll.setOnClickListener(v -> {
            if (songs.isEmpty()) {
                Toast.makeText(this, R.string.toast_no_songs,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            openPlay(0);
        });
    }

    private void loadSongs() {
        String keyword = getIntent().getStringExtra("keyword");
        if (keyword == null || keyword.isEmpty()) {
            keyword = getIntent().getStringExtra("playlist_title");
        }
        if (keyword == null || keyword.isEmpty()) {
            keyword = "经典";
        }

        MusicApi.search(keyword, 20, new MusicApi.SearchCallback() {
            @Override
            public void onSuccess(List<Song> result) {
                songs.clear();
                songs.addAll(result);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(PlaylistDetailActivity.this,
                        message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPlay(int position) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("songs", new Gson().toJson(songs));
        intent.putExtra("index", position);
        startActivity(intent);
    }
}
