package com.example.grow21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressLogin;
    private TextView tvSignupLink;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "grow21_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Check if already logged in
        if (prefs.getBoolean(KEY_LOGGED_IN, false)) {
            navigateAfterLogin();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressLogin = findViewById(R.id.progress_login);
        tvSignupLink = findViewById(R.id.tv_signup_link);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvSignupLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        // Check credentials against local SQLite database
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        boolean valid = db.loginUser(email, password);

        showLoading(false);

        if (valid) {
            prefs.edit()
                    .putBoolean(KEY_LOGGED_IN, true)
                    .putString(KEY_USER_EMAIL, email)
                    .apply();
            navigateAfterLogin();
        } else {
            Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateAfterLogin() {
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Intent intent;
        if (db.childExists()) {
            intent = new Intent(this, StartActivity.class);
        } else {
            intent = new Intent(this, ChildSetupActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        progressLogin.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
    }
}
