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


public class AlbumActivity extends AppCompatActivity {

    private ImageView ivBack, ivCover;
    private TextView tvAlbumName, tvArtist, tvYear, tvTrackCount;
    private RecyclerView rvSongs;

    private final List<Song> songs = new ArrayList<>();
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        ivBack = findViewById(R.id.iv_back);
        ivCover = findViewById(R.id.iv_cover);
        tvAlbumName = findViewById(R.id.tv_album_name);
        tvArtist = findViewById(R.id.tv_artist);
        tvYear = findViewById(R.id.tv_year);
        tvTrackCount = findViewById(R.id.tv_track_count);
        rvSongs = findViewById(R.id.rv_songs);

        ivBack.setOnClickListener(v -> finish());

        // 头部
        String name = getIntent().getStringExtra("album_name");
        String artist = getIntent().getStringExtra("album_artist");
        String year = getIntent().getStringExtra("album_year");
        int coverRes = getIntent().getIntExtra("cover_res", R.drawable.dao);

        if (name == null) name = getString(R.string.album_default_name);
        if (artist == null) artist = "周杰伦";
        if (year == null) year = "2003";

        tvAlbumName.setText(name);
        tvArtist.setText(getString(R.string.album_artist_label, artist));
        tvYear.setText(getString(R.string.album_year_label, year));
        tvTrackCount.setText(getString(R.string.album_track_count, 0));
        ivCover.setImageResource(coverRes);

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

        // 用"专辑名 歌手名"组合关键词，搜索这张专辑里的歌
        String keyword = name + " " + artist;
        MusicApi.search(keyword, 12, new MusicApi.SearchCallback() {
            @Override
            public void onSuccess(List<Song> result) {
                songs.clear();
                songs.addAll(result);
                adapter.notifyDataSetChanged();
                // 列表加载完后更新曲目数
                tvTrackCount.setText(getString(
                        R.string.album_track_count, songs.size()));
            }
            @Override
            public void onError(String message) {
                Toast.makeText(AlbumActivity.this,
                        message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
