package com.example.grow21;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchVoice, switchSound, switchLargeText;
    private SharedPreferences prefs;

    private static final String PREFS_NAME = "grow21_prefs";
    private static final String KEY_VOICE = "voice_instructions";
    private static final String KEY_SOUND = "sound_effects";
    private static final String KEY_LARGE_TEXT = "large_text_mode";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_PARENT_MODE = "is_parent_mode";

    private CardView cardLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        switchVoice = findViewById(R.id.switch_voice);
        switchSound = findViewById(R.id.switch_sound);
        switchLargeText = findViewById(R.id.switch_large_text);
        cardLogout = findViewById(R.id.card_logout);

        // Load current values
        switchVoice.setChecked(prefs.getBoolean(KEY_VOICE, false));
        switchSound.setChecked(prefs.getBoolean(KEY_SOUND, true));
        switchLargeText.setChecked(prefs.getBoolean(KEY_LARGE_TEXT, false));

        // Save changes immediately on toggle
        switchVoice.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_VOICE, isChecked).apply());

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean(KEY_SOUND, isChecked).apply());

        switchLargeText.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_LARGE_TEXT, isChecked).apply();
            // Adjust font scale
            Configuration config = getResources().getConfiguration();
            config.fontScale = isChecked ? 1.3f : 1.0f;
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
            recreate();
        });

        cardLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setPositiveButton(R.string.btn_confirm, (dialog, which) -> {
                    // Clear session
                    prefs.edit()
                            .putBoolean(KEY_LOGGED_IN, false)
                            .putBoolean(KEY_PARENT_MODE, false)
                            .apply();

                    // Navigate to Login and clear back stack
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
