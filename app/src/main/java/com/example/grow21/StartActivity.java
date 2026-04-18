package com.example.grow21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.grow21.models.Child;

public class StartActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private ImageView ivMascot;
    private Button btnStart;

    private static final String PREFS_NAME = "grow21_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tvGreeting = findViewById(R.id.tv_greeting);
        ivMascot = findViewById(R.id.iv_mascot);
        btnStart = findViewById(R.id.btn_start);

        // Load child name from database
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Child child = db.getChild();
        if (child != null) {
            String greeting = getString(R.string.greeting_format, child.getName());
            tvGreeting.setText(greeting);
        }

        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, SkillSelectionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_LOGGED_IN, false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
