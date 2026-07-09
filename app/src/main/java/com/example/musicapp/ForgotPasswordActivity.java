package com.example.musicapp;

import android.os.CountDownTimer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;


public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etPhone;
    private EditText etCode;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnGetCode;
    private Button btnReset;

    // 模拟下发的验证码
    private String mockCode = null;
    // 倒计时器
    private CountDownTimer codeTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ivBack = findViewById(R.id.iv_back);
        etPhone = findViewById(R.id.et_phone);
        etCode = findViewById(R.id.et_code);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnGetCode = findViewById(R.id.btn_get_code);
        btnReset = findViewById(R.id.btn_reset);

        ivBack.setOnClickListener(v -> finish());

        btnGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReset();
            }
        });
    }

    /**
     * 模拟发送验证码：
     *  1) 校验手机号
     *  2) 生成 6 位随机验证码，用 Toast 显示出来
     *  3) 启动 60 秒倒计时，倒计时中按钮不可点
     */
    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        if (phone.length() != 11 || !phone.startsWith("1")) {
            Toast.makeText(this, R.string.toast_phone_invalid,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 生成 6 位验证码
        int n = 100000 + new Random().nextInt(900000);
        mockCode = String.valueOf(n);
        Toast.makeText(this,
                getString(R.string.toast_code_sent, mockCode),
                Toast.LENGTH_LONG).show();

        // 启动 60s 倒计时
        startCountdown();
    }

    private void startCountdown() {
        if (codeTimer != null) codeTimer.cancel();
        btnGetCode.setEnabled(false);
        codeTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                btnGetCode.setText(String.format(Locale.getDefault(),
                        "%ds", sec));
            }
            @Override
            public void onFinish() {
                btnGetCode.setEnabled(true);
                btnGetCode.setText(R.string.btn_get_code);
            }
        };
        codeTimer.start();
    }

    /**
     * 重置密码：校验所有输入
     */
    private void handleReset() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        String pwd = etPassword.getText().toString();
        String confirm = etPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.toast_empty_phone,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, R.string.toast_empty_code,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 校验验证码是否正确
        if (mockCode == null || !mockCode.equals(code)) {
            Toast.makeText(this, R.string.toast_code_wrong,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() < 6) {
            Toast.makeText(this, R.string.toast_password_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.equals(confirm)) {
            Toast.makeText(this, R.string.toast_password_mismatch,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_reset_success,
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消倒计时，避免内存泄漏
        if (codeTimer != null) {
            codeTimer.cancel();
            codeTimer = null;
        }
    }
}
