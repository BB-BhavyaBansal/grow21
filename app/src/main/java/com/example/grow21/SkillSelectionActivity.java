package com.example.grow21;

import android.content.Intent;
import android.os.Bundle;

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

        // Move & Play → vocabulary category
        cardWordplay.setOnClickListener(v -> {
            Intent intent = new Intent(SkillSelectionActivity.this, TapGameActivity.class);
            intent.putExtra("category", "vocabulary");
            startActivity(intent);
        });

        // Vocabulary → vocabulary category (brain card)
        cardBrain.setOnClickListener(v -> {
            Intent intent = new Intent(SkillSelectionActivity.this, TapGameActivity.class);
            intent.putExtra("category", "vocabulary");
            startActivity(intent);
        });

        // Puzzles → shapes category
        cardPuzzles.setOnClickListener(v -> {
            Intent intent = new Intent(SkillSelectionActivity.this, TapGameActivity.class);
            intent.putExtra("category", "shapes");
            startActivity(intent);
        });

        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_menu) {
                // Already on menu
                return true;
            } else if (itemId == R.id.nav_module) {
                // Stay on current screen
                return true;
            } else if (itemId == R.id.nav_report) {
                startActivity(new Intent(this, ProgressActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
    }
}
