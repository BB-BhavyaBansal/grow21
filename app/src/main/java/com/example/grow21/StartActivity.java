package com.example.grow21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.grow21.models.Child;

public class StartActivity extends AppCompatActivity {

    private TextView tvGreeting;
    private ImageView ivMascot;
    private Button btnStart;
    private SwitchCompat switchProfile;

    private static final String PREFS_NAME = "grow21_prefs";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_PARENT_MODE = "is_parent_mode";

    /** Flag to prevent the switch listener from firing during programmatic changes. */
    private boolean isSwitchListenerActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tvGreeting = findViewById(R.id.tv_greeting);
        ivMascot = findViewById(R.id.iv_mascot);
        btnStart = findViewById(R.id.btn_start);
        switchProfile = findViewById(R.id.switch_profile);

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

        setupProfileSwitch();
    }

    /**
     * Sets up the profile switch toggle. When toggled to Parent mode,
     * shows a password verification dialog using the existing login credentials.
     * On successful verification, navigates to ParentDashboardActivity.
     */
    private void setupProfileSwitch() {
        // Ensure switch starts in Child mode (unchecked)
        isSwitchListenerActive = false;
        switchProfile.setChecked(false);
        isSwitchListenerActive = true;

        switchProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSwitchListenerActive) return;

            if (isChecked) {
                // User wants to switch to Parent mode — require password
                showParentAuthDialog();
            }
            // Switching back to Child is handled by returning from ParentDashboardActivity
        });
    }

    /**
     * Displays a password verification dialog. Uses the existing user email
     * from SharedPreferences and validates against the User table.
     */
    private void showParentAuthDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_parent_pin, null);

        EditText etPassword = dialogView.findViewById(R.id.et_pin_password);
        Button btnConfirm = dialogView.findViewById(R.id.btn_pin_confirm);
        Button btnCancel = dialogView.findViewById(R.id.btn_pin_cancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnConfirm.setOnClickListener(v -> {
            String password = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }

            // Verify against the stored user credentials
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String userEmail = prefs.getString(KEY_USER_EMAIL, "");

            DatabaseHelper db = DatabaseHelper.getInstance(this);
            boolean valid = db.loginUser(userEmail, password);

            if (valid) {
                dialog.dismiss();
                prefs.edit().putBoolean(KEY_PARENT_MODE, true).apply();

                // Navigate to Parent Dashboard
                Intent intent = new Intent(StartActivity.this, ParentDashboardActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.error_wrong_password, Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
            // Reset switch to Child mode without triggering the listener
            isSwitchListenerActive = false;
            switchProfile.setChecked(false);
            isSwitchListenerActive = true;
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_LOGGED_IN, false)) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Reset switch to Child mode when returning from Parent Dashboard
        isSwitchListenerActive = false;
        switchProfile.setChecked(false);
        isSwitchListenerActive = true;
        prefs.edit().putBoolean(KEY_PARENT_MODE, false).apply();
    }
}
