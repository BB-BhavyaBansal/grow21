package com.example.grow21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChildSetupActivity extends AppCompatActivity {

    private EditText etChildName;
    private TextView tvAgeValue;
    private Button btnAgeMinus, btnAgePlus, btnSave;
    private ImageView avatar1, avatar2, avatar3, avatar4;

    private int selectedAge = 5;
    private int selectedAvatar = 0;
    private ImageView[] avatarViews;

    private static final String PREFS_NAME = "grow21_prefs";
    private static final String KEY_SETUP_COMPLETE = "setup_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_setup);

        etChildName = findViewById(R.id.et_child_name);
        tvAgeValue = findViewById(R.id.tv_age_value);
        btnAgeMinus = findViewById(R.id.btn_age_minus);
        btnAgePlus = findViewById(R.id.btn_age_plus);
        btnSave = findViewById(R.id.btn_save_child);

        avatar1 = findViewById(R.id.avatar_1);
        avatar2 = findViewById(R.id.avatar_2);
        avatar3 = findViewById(R.id.avatar_3);
        avatar4 = findViewById(R.id.avatar_4);

        avatarViews = new ImageView[]{avatar1, avatar2, avatar3, avatar4};

        tvAgeValue.setText(String.valueOf(selectedAge));

        btnAgeMinus.setOnClickListener(v -> {
            if (selectedAge > 3) {
                selectedAge--;
                tvAgeValue.setText(String.valueOf(selectedAge));
            }
        });

        btnAgePlus.setOnClickListener(v -> {
            if (selectedAge < 18) {
                selectedAge++;
                tvAgeValue.setText(String.valueOf(selectedAge));
            }
        });

        // Avatar selection
        for (int i = 0; i < avatarViews.length; i++) {
            final int index = i;
            avatarViews[i].setOnClickListener(v -> selectAvatar(index));
        }

        btnSave.setOnClickListener(v -> saveChild());
    }

    private void selectAvatar(int index) {
        selectedAvatar = index;
        for (int i = 0; i < avatarViews.length; i++) {
            if (i == index) {
                avatarViews[i].setAlpha(1.0f);
                avatarViews[i].setScaleX(1.15f);
                avatarViews[i].setScaleY(1.15f);
            } else {
                avatarViews[i].setAlpha(0.5f);
                avatarViews[i].setScaleX(1.0f);
                avatarViews[i].setScaleY(1.0f);
            }
        }
    }

    private void saveChild() {
        String name = etChildName.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, R.string.error_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAge < 3 || selectedAge > 18) {
            Toast.makeText(this, R.string.error_age_invalid, Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        db.insertChild(name, selectedAge, selectedAvatar);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_SETUP_COMPLETE, true).apply();

        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}
