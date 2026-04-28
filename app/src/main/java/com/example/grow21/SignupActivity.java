package com.example.grow21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;
    private TextView tvLoginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.et_signup_email);
        etPassword = findViewById(R.id.et_signup_password);
        etConfirmPassword = findViewById(R.id.et_signup_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        tvLoginLink = findViewById(R.id.tv_login_link);

        btnSignup.setOnClickListener(v -> attemptSignup());

        tvLoginLink.setOnClickListener(v -> {
            finish(); // Go back to LoginActivity
        });
    }

    private void attemptSignup() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, R.string.error_password_short, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, R.string.error_passwords_mismatch, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this);

        if (db.userExists(email)) {
            Toast.makeText(this, R.string.error_email_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = db.registerUser(email, password);
        if (success) {
            SharedPreferences prefs = getSharedPreferences("grow21_prefs", MODE_PRIVATE);
            prefs.edit()
                 .putBoolean("is_logged_in", true)
                 .putString("user_email", email)
                 .apply();

            Toast.makeText(this, R.string.signup_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ChildSetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
