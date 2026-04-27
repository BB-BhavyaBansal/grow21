package com.example.grow21;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SkillSelectionActivity extends AppCompatActivity {

    private CardView cardWordplay, cardBrain, cardPuzzles;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_selection);

        cardWordplay = findViewById(R.id.card_wordplay);
        cardBrain = findViewById(R.id.card_brain);
        cardPuzzles = findViewById(R.id.card_puzzles);

        bottomNavigation = findViewById(R.id.bottom_navigation);

        // 🎮 Move & Play → Drag Game
        cardWordplay.setOnClickListener(v ->
                startActivity(new Intent(this, DragGameActivity.class))
        );

        // 🧠 Vocabulary → Tap Game
        cardBrain.setOnClickListener(v -> {
            Intent intent = new Intent(this, TapGameActivity.class);
            intent.putExtra("category", "word");
            startActivity(intent);
        });

        // 🧩 Puzzle → Memory Game
        cardPuzzles.setOnClickListener(v ->
                startActivity(new Intent(this, MemoryGameActivity.class))
        );

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_menu) return true;

            else if (id == R.id.nav_module) return true;

            else if (id == R.id.nav_report) {
                startActivity(new Intent(this, ProgressActivity.class));
                return true;
            }

            else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNavigationVisibility();
    }

    private void updateNavigationVisibility() {
        SharedPreferences prefs = getSharedPreferences("grow21_prefs", MODE_PRIVATE);
        boolean isParentMode = prefs.getBoolean("is_parent_mode", false);

        Menu menu = bottomNavigation.getMenu();
        menu.findItem(R.id.nav_report).setVisible(isParentMode);
        menu.findItem(R.id.nav_profile).setVisible(isParentMode);
    }
}