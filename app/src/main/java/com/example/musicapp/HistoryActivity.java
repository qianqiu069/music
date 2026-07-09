package com.example.musicapp;

import android.app.AlertDialog;
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

/**
 * HistoryActivity - 播放历史页
 *
 * 学习要点：
 * 1) AlertDialog 弹出二次确认（防止误清空）
 * 2) 通过移除 LinearLayout 的所有子 View 来"清空"列表
 *    然后切到空态 TextView 显示提示
 */
public class HistoryActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvClear;
    private TextView tvSummary;
    private TextView tvEmpty;
    private LinearLayout llSongs;

    private final List<String[]> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ivBack = findViewById(R.id.iv_back);
        tvClear = findViewById(R.id.tv_clear);
        tvSummary = findViewById(R.id.tv_summary);
        tvEmpty = findViewById(R.id.tv_empty);
        llSongs = findViewById(R.id.ll_songs);

        ivBack.setOnClickListener(v -> finish());
        tvClear.setOnClickListener(v -> confirmClear());

        // 示例历史数据：歌名 + 歌手 + 播放时间
        history.add(new String[]{"晴天", "周杰伦", "今天 14:32"});
        history.add(new String[]{"起风了", "买辣椒也用券", "今天 11:08"});
        history.add(new String[]{"夜空中最亮的星", "逃跑计划", "昨天 22:15"});
        history.add(new String[]{"成都", "赵雷", "昨天 20:40"});
        history.add(new String[]{"海阔天空", "Beyond", "昨天 18:21"});
        history.add(new String[]{"演员", "薛之谦", "2 天前 21:05"});
        history.add(new String[]{"后来", "刘若英", "2 天前 19:33"});

        render();
    }

    /** 弹出确认对话框 */
    private void confirmClear() {
        if (history.isEmpty()) {
            Toast.makeText(this, R.string.toast_history_already_empty,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.history_clear)
                .setMessage(R.string.dialog_history_clear_msg)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    history.clear();
                    render();
                    Toast.makeText(this, R.string.toast_history_cleared,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    /** 重新渲染整个列表 */
    private void render() {
        llSongs.removeAllViews();

        if (history.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            llSongs.setVisibility(View.GONE);
            tvSummary.setText(getString(R.string.history_summary, 0));
            return;
        }

        tvEmpty.setVisibility(View.GONE);
        llSongs.setVisibility(View.VISIBLE);
        tvSummary.setText(getString(R.string.history_summary, history.size()));

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < history.size(); i++) {
            String[] item = history.get(i);
            View row = inflater.inflate(R.layout.item_song, llSongs, false);

            TextView tvIndex = row.findViewById(R.id.tv_index);
            TextView tvName = row.findViewById(R.id.tv_song_name);
            TextView tvArtist = row.findViewById(R.id.tv_artist);

            tvIndex.setText(String.valueOf(i + 1));
            tvName.setText(item[0]);
            // "歌手 · 时间" —— 复用副标题位置
            tvArtist.setText(item[1] + " · " + item[2]);

            llSongs.addView(row);
        }
    }
}
