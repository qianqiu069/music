package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;


public class LoginActivity extends AppCompatActivity {

    // 控件成员变量
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 把布局文件渲染到当前 Activity
        setContentView(R.layout.activity_login);

        // 第 1 步：绑定控件
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);

        // 第 2 步：登录按钮点击事件
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        // 第 3 步：注册文本 -> 跳转到注册页
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,
                        RegisterActivity.class));
            }
        });

        // 第 4 步："忘记密码" -> 找回密码页（控件在布局新增）
        TextView tvForgot = findViewById(R.id.tv_forgot);
        if (tvForgot != null) {
            tvForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this,
                            ForgotPasswordActivity.class));
                }
            });
        }
    }

    private void handleLogin() {
        // .toString().trim() 去掉前后空格
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // TextUtils.isEmpty 等价于 username == null || username.length() == 0
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.toast_empty_username, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.toast_empty_password, Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, R.string.toast_login_success, Toast.LENGTH_SHORT).show();

        // 登录后进入歌单推荐页
        Intent intent = new Intent(LoginActivity.this, PlaylistActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);

        // 关闭登录页，避免按返回键回到登录页
        finish();
    }
}
