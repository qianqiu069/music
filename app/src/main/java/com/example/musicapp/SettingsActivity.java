package com.example.musicapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Locale;
import java.util.Random;

public class SettingsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RadioGroup rgQuality;
    private SwitchCompat swWifiOnly;
    private SwitchCompat swDarkMode;
    private TextView tvCacheSize;
    private android.view.View rowClearCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ivBack = findViewById(R.id.iv_back);
        rgQuality = findViewById(R.id.rg_quality);
        swWifiOnly = findViewById(R.id.sw_wifi_only);
        swDarkMode = findViewById(R.id.sw_dark_mode);
        tvCacheSize = findViewById(R.id.tv_cache_size);
        rowClearCache = findViewById(R.id.row_clear_cache);

        ivBack.setOnClickListener(v -> finish());

        // 模拟一个缓存大小
        tvCacheSize.setText(makeFakeCacheSize());

        // 音质切换
        rgQuality.setOnCheckedChangeListener((group, checkedId) -> {
            int msgRes;
            if (checkedId == R.id.rb_high) {
                msgRes = R.string.quality_high;
            } else if (checkedId == R.id.rb_lossless) {
                msgRes = R.string.quality_lossless;
            } else {
                msgRes = R.string.quality_standard;
            }
            Toast.makeText(this,
                    getString(R.string.toast_quality_changed, getString(msgRes)),
                    Toast.LENGTH_SHORT).show();
        });

        // 仅 WiFi 下播放
        swWifiOnly.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this,
                    isChecked ? R.string.toast_wifi_only_on
                              : R.string.toast_wifi_only_off,
                    Toast.LENGTH_SHORT).show();
        });

        // 深色模式
        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this,
                    isChecked ? R.string.toast_dark_on
                              : R.string.toast_dark_off,
                    Toast.LENGTH_SHORT).show();
        });

        // 清除缓存
        rowClearCache.setOnClickListener(v -> confirmClearCache());
    }

    /** 弹出确认对话框 -> 清缓存 */
    private void confirmClearCache() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.settings_clear_cache)
                .setMessage(R.string.dialog_clear_cache_msg)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    tvCacheSize.setText("0 MB");
                    Toast.makeText(this, R.string.toast_cache_cleared,
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();
    }

    /** 随机缓存 */
    private String makeFakeCacheSize() {
        int mb = 8 + new Random().nextInt(72);
        return String.format(Locale.getDefault(), "%d MB", mb);
    }
}
