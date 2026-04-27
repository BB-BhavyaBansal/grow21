package com.example.grow21;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.grow21.models.QuestionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TapGameActivity extends AppCompatActivity {

    private TextView tvQuestion;
    private ProgressBar progressBar;

    private CardView[] optionCards;
    private TextView[] optionTexts;

    private List<QuestionModel> questions;
    private int currentQuestionIndex = 0;
    private int level = 1;
    private String category;
    private boolean isAnswered = false;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_game);

        handler = new Handler(Looper.getMainLooper());

        category = getIntent().getStringExtra("category");
        if (category == null) category = "word";

        initViews();
        loadQuestions();
    }

    private void initViews() {
        tvQuestion = findViewById(R.id.tv_question);
        progressBar = findViewById(R.id.progress_bar);

        optionCards = new CardView[]{
                findViewById(R.id.card_option_1),
                findViewById(R.id.card_option_2),
                findViewById(R.id.card_option_3),
                findViewById(R.id.card_option_4)
        };

        optionTexts = new TextView[]{
                findViewById(R.id.tv_option_1),
                findViewById(R.id.tv_option_2),
                findViewById(R.id.tv_option_3),
                findViewById(R.id.tv_option_4)
        };

        for (int i = 0; i < 4; i++) {
            final int index = i;
            optionCards[i].setOnClickListener(v -> {
                if (!isAnswered) checkAnswer(index);
            });
        }
    }

    private void loadQuestions() {

        questions = new ArrayList<>();

        if ("motor".equals(category)) {

            questions.add(new QuestionModel("Tap red", "", Arrays.asList("Red","Blue","Green","Yellow"), "Red"));
            questions.add(new QuestionModel("Tap circle", "", Arrays.asList("Circle","Square","Triangle","Star"), "Circle"));

        } else if ("cognitive".equals(category)) {

            questions.add(new QuestionModel("Tap same", "", Arrays.asList("Apple","Apple","Dog","Cat"), "Apple"));
            questions.add(new QuestionModel("Odd one?", "", Arrays.asList("Apple","Apple","Apple","Dog"), "Dog"));

        } else {

            questions.add(new QuestionModel("Which is apple?", "", Arrays.asList("Apple","Ball","Dog","Cat"), "Apple"));
            questions.add(new QuestionModel("Which is animal?", "", Arrays.asList("Dog","Chair","Book","Pen"), "Dog"));
        }

        Collections.shuffle(questions);
        displayQuestion();
    }

    private void displayQuestion() {

        if (currentQuestionIndex >= questions.size()) {
            finish();
            return;
        }

        isAnswered = false;

        QuestionModel q = questions.get(currentQuestionIndex);

        tvQuestion.setText(q.getQuestion() + " (Level " + level + ")");

        for (int i = 0; i < 4; i++) {
            optionTexts[i].setText(q.getOptions().get(i));
        }

        progressBar.setProgress((currentQuestionIndex * 100) / questions.size());
    }

    private void checkAnswer(int index) {

        isAnswered = true;

        QuestionModel q = questions.get(currentQuestionIndex);

        String selected = optionTexts[index].getText().toString();
        String correct = q.getAnswer();

        boolean isCorrect = selected.equals(correct);

        if ("motor".equals(category)) isCorrect = true;

        if (isCorrect) {
            Toast.makeText(this, "Great Job!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Good Try!", Toast.LENGTH_SHORT).show();
        }

        currentQuestionIndex++;

        if (currentQuestionIndex % 2 == 0 && level < 3) {
            level++;
        }

        handler.postDelayed(this::displayQuestion, 800);
    }
}