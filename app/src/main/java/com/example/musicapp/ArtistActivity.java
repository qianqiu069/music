package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
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


public class ArtistActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvTopTitle;
    private TextView tvName;
    private TextView tvBio;
    private RecyclerView rvSongs;

    private final List<Song> songs = new ArrayList<>();
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        ivBack = findViewById(R.id.iv_back);
        tvTopTitle = findViewById(R.id.tv_top_title);
        tvName = findViewById(R.id.tv_name);
        tvBio = findViewById(R.id.tv_bio);
        rvSongs = findViewById(R.id.rv_songs);

        ivBack.setOnClickListener(v -> finish());

        // 头部数据
        String name = getIntent().getStringExtra("artist_name");
        String bio = getIntent().getStringExtra("artist_bio");
        if (name == null || name.isEmpty()) name = "周杰伦";
        if (bio == null || bio.isEmpty()) {
            bio = getString(R.string.artist_default_bio);
        }
        tvName.setText(name);
        tvBio.setText(bio);
        tvTopTitle.setText(name);

        // 列表
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

        // 搜索这位歌手的歌
        MusicApi.search(name, 20, new MusicApi.SearchCallback() {
            @Override
            public void onSuccess(List<Song> result) {
                songs.clear();
                songs.addAll(result);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(ArtistActivity.this,
                        message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
