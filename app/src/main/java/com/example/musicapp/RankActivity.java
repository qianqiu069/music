package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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


public class RankActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tabSoar, tabHot, tabNew;
    private TextView tvStatus;
    private RecyclerView rvSongs;

    private final List<Song> songs = new ArrayList<>();
    private SongAdapter adapter;

    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        ivBack = findViewById(R.id.iv_back);
        tabSoar = findViewById(R.id.tab_soar);
        tabHot = findViewById(R.id.tab_hot);
        tabNew = findViewById(R.id.tab_new);
        tvStatus = findViewById(R.id.tv_status);
        rvSongs = findViewById(R.id.rv_songs);

        ivBack.setOnClickListener(v -> finish());

        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SongAdapter(songs);
        rvSongs.setAdapter(adapter);

        adapter.setOnItemClickListener((position, song) -> {
            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra("songs", new Gson().toJson(songs));
            intent.putExtra("index", position);
            startActivity(intent);
        });

        tabSoar.setOnClickListener(v -> selectTab(0));
        tabHot.setOnClickListener(v -> selectTab(1));
        tabNew.setOnClickListener(v -> selectTab(2));

        // 默认加载飙升榜
        selectTab(0);
    }


    private void selectTab(int tab) {
        currentTab = tab;
        int normal = getResources().getColor(R.color.textSecondary);
        int active = getResources().getColor(R.color.neteaseRed);
        tabSoar.setTextColor(tab == 0 ? active : normal);
        tabHot.setTextColor(tab == 1 ? active : normal);
        tabNew.setTextColor(tab == 2 ? active : normal);

        String keyword;
        switch (tab) {
            case 1: keyword = "热门"; break;
            case 2: keyword = "新歌"; break;
            case 0:
            default: keyword = "流行"; break;
        }

        // 清空列表，显示加载中
        songs.clear();
        adapter.notifyDataSetChanged();
        tvStatus.setText(R.string.text_loading);
        tvStatus.setVisibility(View.VISIBLE);
        rvSongs.setVisibility(View.GONE);

        MusicApi.search(keyword, 20, new MusicApi.SearchCallback() {
            @Override
            public void onSuccess(List<Song> result) {
                songs.clear();
                songs.addAll(result);
                adapter.notifyDataSetChanged();
                tvStatus.setVisibility(View.GONE);
                rvSongs.setVisibility(View.VISIBLE);
            }
            @Override
            public void onError(String message) {
                tvStatus.setText(message);
                tvStatus.setVisibility(View.VISIBLE);
                rvSongs.setVisibility(View.GONE);
            }
        });
    }
}
