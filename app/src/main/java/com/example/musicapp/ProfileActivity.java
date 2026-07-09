package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class ProfileActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvUsername;
    private View rowFavorites;
    private View rowHistory;
    private View rowRank;
    private View rowSettings;
    private View rowAbout;
    private View rowArtist;
    private View rowAlbum;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        bindViews();
        setupUser();
        setupClicks();
        setupBottomTabs();
    }


    private void setupBottomTabs() {
        View tabHome = findViewById(R.id.tab_home);
        View tabProfile = findViewById(R.id.tab_profile);

        if (tabProfile != null) {
            tabProfile.setSelected(true);
            tabProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ProfileActivity.this,
                            "已经在我的页啦", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (tabHome != null) {
            tabHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    private void bindViews() {
        ivBack = findViewById(R.id.iv_back);
        tvUsername = findViewById(R.id.tv_username);
        rowFavorites = findViewById(R.id.row_favorites);
        rowHistory = findViewById(R.id.row_history);
        rowRank = findViewById(R.id.row_rank);
        rowSettings = findViewById(R.id.row_settings);
        rowAbout = findViewById(R.id.row_about);
        rowArtist = findViewById(R.id.row_artist);
        rowAlbum = findViewById(R.id.row_album);
        btnLogout = findViewById(R.id.btn_logout);
    }

    /**
     * 取 LoginActivity 传过来的 username 显示在头像旁
     * 没有就用默认昵称
     */
    private void setupUser() {
        String username = getIntent().getStringExtra("username");
        if (!TextUtils.isEmpty(username)) {
            tvUsername.setText(username);
        }
    }

    private void setupClicks() {
        ivBack.setOnClickListener(v -> finish());

        rowFavorites.setOnClickListener(v -> startActivity(
                new Intent(this, FavoritesActivity.class)));

        rowHistory.setOnClickListener(v -> startActivity(
                new Intent(this, HistoryActivity.class)));

        rowRank.setOnClickListener(v -> startActivity(
                new Intent(this, RankActivity.class)));

        rowSettings.setOnClickListener(v -> startActivity(
                new Intent(this, SettingsActivity.class)));

        rowAbout.setOnClickListener(v -> startActivity(
                new Intent(this, AboutActivity.class)));

        rowArtist.setOnClickListener(v -> {
            Intent intent = new Intent(this, ArtistActivity.class);
            intent.putExtra("artist_name", "周杰伦");
            startActivity(intent);
        });

        // 专辑详情
        rowAlbum.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlbumActivity.class);
            intent.putExtra("album_name", "叶惠美");
            intent.putExtra("album_artist", "周杰伦");
            intent.putExtra("album_year", "2003");
            intent.putExtra("cover_res", R.drawable.dao);
            startActivity(intent);
        });

        // 退出登录：回到登录页，并清掉中间所有 Activity
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            // FLAG_ACTIVITY_CLEAR_TOP + NEW_TASK：清掉栈里其它 Activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
