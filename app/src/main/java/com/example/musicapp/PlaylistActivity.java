package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        //右上角放大镜 -> 跳到 MainActivity 搜索页
        ImageView ivSearch = findViewById(R.id.iv_search);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        setupBottomTabs();

        //填充 6 张歌单卡片，并绑定跳转
        bindPlaylistCard(R.id.card_fan,
                R.drawable.fan, "翻唱精选", "好声音重新演绎的经典");
        bindPlaylistCard(R.id.card_dao,
                R.drawable.dao, "经典老歌", "永不过时的旋律回忆");
        bindPlaylistCard(R.id.card_man,
                R.drawable.man, "醇厚男声", "低音炮 / 民谣男声合集");
        bindPlaylistCard(R.id.card_girl,
                R.drawable.girl, "治愈女声", "夜晚听完整个世界都静下来");
        bindPlaylistCard(R.id.card_er,
                R.drawable.er, "儿童乐园", "陪伴小朋友的童年金曲");
        bindPlaylistCard(R.id.card_free,
                R.drawable.free, "自由放歌", "释放压力的轻松旋律");

        fillHotSongs();
    }

    /**
     * 绑定底部两个 Tab 的点击行为
     */
    private void setupBottomTabs() {
        View tabHome = findViewById(R.id.tab_home);
        if (tabHome != null) {
            tabHome.setSelected(true);
            tabHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 已经在首页：滚回顶部 + 提示，不重复跳转
                    ScrollView sv = findViewById(R.id.sv_content);
                    if (sv != null) {
                        sv.smoothScrollTo(0, 0);
                    }
                    Toast.makeText(PlaylistActivity.this,
                            "已经在首页啦", Toast.LENGTH_SHORT).show();
                }
            });
        }

        View tabProfile = findViewById(R.id.tv_profile);
        if (tabProfile != null) {
            tabProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(
                            PlaylistActivity.this, ProfileActivity.class);
                    intent.putExtra("username",
                            getIntent().getStringExtra("username"));
                    // 配合 SINGLE_TOP，避免重复创建多个 ProfileActivity 实例
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View tabHome = findViewById(R.id.tab_home);
        View tabProfile = findViewById(R.id.tv_profile);
        if (tabHome != null) tabHome.setSelected(true);
        if (tabProfile != null) tabProfile.setSelected(false);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    private void bindPlaylistCard(int cardId, int coverRes,
                                  String title, String desc) {
        View card = findViewById(cardId);
        if (card == null) return;

        ImageView ivCover = card.findViewById(R.id.iv_cover);
        TextView tvTitle = card.findViewById(R.id.tv_title);
        TextView tvDesc = card.findViewById(R.id.tv_desc);

        ivCover.setImageResource(coverRes);
        tvTitle.setText(title);
        tvDesc.setText(desc);

        // 点击 -> 跳到歌单详情页，把标题、副标题、封面 resId 都带过去
        card.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PlaylistActivity.this, PlaylistDetailActivity.class);
            intent.putExtra("playlist_title", title);
            intent.putExtra("playlist_desc", desc);
            intent.putExtra("cover_res", coverRes);
            // 拿歌单标题作为搜索关键词
            intent.putExtra("keyword", title);
            startActivity(intent);
        });
    }

    /**
     * 把热榜歌曲渲染到列表容器里
     * 这里直接用 item_song.xml 复用，方便保持视觉一致
     */
    private void fillHotSongs() {
        LinearLayout container = findViewById(R.id.ll_hot_songs);
        if (container == null) return;

        // 候选热榜（歌名 + 歌手）
        List<String[]> pool = new ArrayList<>();
        pool.add(new String[]{"晴天", "周杰伦"});
        pool.add(new String[]{"海阔天空", "Beyond"});
        pool.add(new String[]{"起风了", "买辣椒也用券"});
        pool.add(new String[]{"稻香", "周杰伦"});
        pool.add(new String[]{"后来", "刘若英"});
        pool.add(new String[]{"月亮代表我的心", "邓丽君"});
        pool.add(new String[]{"听妈妈的话", "周杰伦"});
        pool.add(new String[]{"平凡之路", "朴树"});
        pool.add(new String[]{"成都", "赵雷"});
        pool.add(new String[]{"小幸运", "田馥甄"});
        pool.add(new String[]{"演员", "薛之谦"});
        pool.add(new String[]{"光辉岁月", "Beyond"});

        // 打乱后取前 6 条
        Collections.shuffle(pool);
        int count = Math.min(6, pool.size());

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < count; i++) {
            String[] song = pool.get(i);

            View row = inflater.inflate(R.layout.item_song, container, false);
            TextView tvIndex = row.findViewById(R.id.tv_index);
            TextView tvSongName = row.findViewById(R.id.tv_song_name);
            TextView tvArtist = row.findViewById(R.id.tv_artist);

            tvIndex.setText(String.valueOf(i + 1));
            tvSongName.setText(song[0]);
            tvArtist.setText(song[1]);

            container.addView(row);
        }
    }
}
