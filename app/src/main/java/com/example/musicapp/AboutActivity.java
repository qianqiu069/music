package com.example.musicapp;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView ivBack = findViewById(R.id.iv_back);
        TextView tvVersion = findViewById(R.id.tv_version);

        ivBack.setOnClickListener(v -> finish());

        // 自动从 manifest 读出 versionName
        tvVersion.setText(getString(R.string.about_version, readVersionName()));
    }

    private String readVersionName() {
        try {
            PackageInfo info = getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            return info.versionName == null ? "1.0" : info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0";
        }
    }
}
