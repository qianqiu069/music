package com.example.musicapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {

    private ImageView ivBack;
    private EditText etUsername;
    private EditText etPhone;
    private EditText etPassword;
    private EditText etPasswordConfirm;
    private Button btnRegister;
    private TextView tvGoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 第 1 步：绑定控件
        ivBack = findViewById(R.id.iv_back);
        etUsername = findViewById(R.id.et_username);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etPasswordConfirm = findViewById(R.id.et_password_confirm);
        btnRegister = findViewById(R.id.btn_register);
        tvGoLogin = findViewById(R.id.tv_go_login);

        // 第 2 步：返回按钮
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 第 3 步：注册按钮
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegister();
            }
        });

        // 第 4 步：去登录
        tvGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 直接 finish 回到 LoginActivity（它仍在栈里）
                finish();
            }
        });
    }

    /**
     * 注册逻辑：依次校验四个输入框
     */
    private void handleRegister() {
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString();
        String confirm = etPasswordConfirm.getText().toString();

        // 用户名
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.toast_empty_username,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // 用户名长度限制
        if (username.length() < 2) {
            Toast.makeText(this, R.string.toast_username_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 手机号
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.toast_empty_phone,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() != 11 || !phone.startsWith("1")) {
            Toast.makeText(this, R.string.toast_phone_invalid,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 密码
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.toast_empty_password,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, R.string.toast_password_too_short,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 两次密码一致：字符串比较用 equals，不能用 ==
        if (!password.equals(confirm)) {
            Toast.makeText(this, R.string.toast_password_mismatch,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 模拟注册成功
        Toast.makeText(this, R.string.toast_register_success,
                Toast.LENGTH_SHORT).show();
        // 回到登录页
        finish();
    }
}
