package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapp.R;
import com.example.musicapp.adapter.SongAdapter;
import com.example.musicapp.model.Song;
import com.example.musicapp.utils.MusicApi;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private Button btnSearch;
    private RecyclerView rvSongs;
    private TextView tvEmpty;

    // 数据列表
    private final List<Song> songs = new ArrayList<>();
    private SongAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 找控件
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        rvSongs = findViewById(R.id.rv_songs);
        tvEmpty = findViewById(R.id.tv_empty);

        // 初始化 RecyclerView：垂直线性布局 + 分割线
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        rvSongs.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // 初始化适配器，绑定空数据
        adapter = new SongAdapter(songs);
        rvSongs.setAdapter(adapter);

        // 点击列表项 -> 跳到播放页
        adapter.setOnItemClickListener(new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Song song) {
                openPlayActivity(position);
            }
        });

        // 搜索按钮点击
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });
    }

    private void doSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        hideKeyboard();

        tvEmpty.setText("加载中…");
        tvEmpty.setVisibility(View.VISIBLE);
        rvSongs.setVisibility(View.GONE);

        // 发起搜索（异步），10 首歌
        MusicApi.search(keyword, 10, new MusicApi.SearchCallback() {
            @Override
            public void onSuccess(List<Song> result) {
                // 切回主线程的逻辑已经在 MusicApi 里处理
                songs.clear();
                songs.addAll(result);
                adapter.notifyDataSetChanged();

                tvEmpty.setVisibility(View.GONE);
                rvSongs.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                tvEmpty.setText(message);
                tvEmpty.setVisibility(View.VISIBLE);
                rvSongs.setVisibility(View.GONE);
            }
        });
    }


    private void openPlayActivity(int position) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra("songs", new Gson().toJson(songs));
        intent.putExtra("index", position);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
