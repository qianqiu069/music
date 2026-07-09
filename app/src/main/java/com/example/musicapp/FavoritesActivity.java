package com.example.musicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class FavoritesActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvCount;
    private TextView tvEmpty;
    private LinearLayout llSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ivBack = findViewById(R.id.iv_back);
        tvCount = findViewById(R.id.tv_count);
        tvEmpty = findViewById(R.id.tv_empty);
        llSongs = findViewById(R.id.ll_songs);

        ivBack.setOnClickListener(v -> finish());

        // 示例收藏数据：歌名 + 歌手
        List<String[]> mock = new ArrayList<>();
        mock.add(new String[]{"晴天", "周杰伦"});
        mock.add(new String[]{"夜曲", "周杰伦"});
        mock.add(new String[]{"成都", "赵雷"});
        mock.add(new String[]{"演员", "薛之谦"});
        mock.add(new String[]{"光辉岁月", "Beyond"});
        mock.add(new String[]{"小幸运", "田馥甄"});
        mock.add(new String[]{"平凡之路", "朴树"});
        mock.add(new String[]{"安静", "周杰伦"});

        renderList(mock);
    }

    /**
     * 渲染收藏列表
     *  - 空时显示空态
     *  - 非空时填充列表 + 顶部数量
     */
    private void renderList(List<String[]> data) {
        if (data == null || data.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            llSongs.setVisibility(View.GONE);
            tvCount.setText(getString(R.string.favorites_count, 0));
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        llSongs.setVisibility(View.VISIBLE);
        tvCount.setText(getString(R.string.favorites_count, data.size()));

        // 清空旧数据，避免重复 inflate
        llSongs.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < data.size(); i++) {
            String[] song = data.get(i);

            View row = inflater.inflate(R.layout.item_song, llSongs, false);
            TextView tvIndex = row.findViewById(R.id.tv_index);
            TextView tvName = row.findViewById(R.id.tv_song_name);
            TextView tvArtist = row.findViewById(R.id.tv_artist);

            tvIndex.setText(String.valueOf(i + 1));
            tvName.setText(song[0]);
            tvArtist.setText(song[1]);

            // 点击 -> 提示：示例数据，建议从搜索页查找
            final String name = song[0];
            row.setOnClickListener(v -> Toast.makeText(this,
                    getString(R.string.toast_demo_song, name),
                    Toast.LENGTH_SHORT).show());

            llSongs.addView(row);
        }
    }
}
