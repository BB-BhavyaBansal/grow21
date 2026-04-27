package com.example.grow21;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DragGameActivity extends AppCompatActivity {

    private ImageView draggable, target;
    private TextView tvInstruction;

    private int currentLevel = 0;
    private int level = 1;

    private float dX, dY;

    private int[] images = {
            R.drawable.apple,
            R.drawable.ball,
            R.drawable.cat,
            R.drawable.dog,
            R.drawable.book
    };

    private String[] instructions = {
            "Drag the apple into the basket",
            "Drag the ball into the basket",
            "Drag the cat into the basket",
            "Drag the dog into the basket",
            "Drag the book into the basket"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_game);

        draggable = findViewById(R.id.draggable);
        target = findViewById(R.id.target);
        tvInstruction = findViewById(R.id.tv_instruction);

        loadLevel();

        draggable.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    dX = view.getX() - event.getRawX();
                    dY = view.getY() - event.getRawY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    view.setX(event.getRawX() + dX);
                    view.setY(event.getRawY() + dY);
                    break;

                case MotionEvent.ACTION_UP:

                    if (isOverlapping(view, target)) {

                        view.animate()
                                .x(target.getX())
                                .y(target.getY())
                                .setDuration(200)
                                .start();

                        Toast.makeText(this, "🎉 Great Job!", Toast.LENGTH_SHORT).show();

                        currentLevel++;

                        // 🔥 LEVEL PROGRESSION
                        if (currentLevel % 2 == 0 && level < 3) {
                            level++;
                        }

                        if (currentLevel < images.length) {
                            view.postDelayed(this::loadLevel, 500);
                        } else {
                            Toast.makeText(this, "All Done!", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                    break;
            }

            return true;
        });
    }

    private void loadLevel() {

        draggable.setImageResource(images[currentLevel]);
        tvInstruction.setText(instructions[currentLevel] + " (Level " + level + ")");

        draggable.post(() -> {

            if (level == 1) {
                draggable.setScaleX(1.2f);
                draggable.setScaleY(1.2f);
                draggable.setX(300);
                draggable.setY(900);
            }

            else if (level == 2) {
                draggable.setScaleX(1f);
                draggable.setScaleY(1f);
                draggable.setX(150);
                draggable.setY(1000);
            }

            else {
                draggable.setScaleX(0.8f);
                draggable.setScaleY(0.8f);
                draggable.setX(50);
                draggable.setY(1100);
            }
        });
    }

    private boolean isOverlapping(View v1, View v2) {
        return v1.getX() < v2.getX() + v2.getWidth() &&
                v1.getX() + v1.getWidth() > v2.getX() &&
                v1.getY() < v2.getY() + v2.getHeight() &&
                v1.getY() + v1.getHeight() > v2.getY();
    }
}