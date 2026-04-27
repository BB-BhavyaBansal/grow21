package com.example.grow21;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.grow21.models.QuestionModel;

import java.util.List;
import java.util.Locale;

public class TapGameActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ImageButton btnClose;
    private ProgressBar progressBar;
    private ImageView ivHeart;
    private TextView tvQuestion;
    private ImageView ivQuestionImage;

    private CardView cardOption1, cardOption2, cardOption3, cardOption4;
    private FrameLayout frameOption1, frameOption2, frameOption3, frameOption4;
    private TextView tvOption1, tvOption2, tvOption3, tvOption4;

    private List<QuestionModel> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int hearts = 3;
    private String category;
    private boolean isAnswered = false;

    private DatabaseHelper dbHelper;
    private Handler handler;
    private TextToSpeech tts;
    private boolean ttsEnabled = false;

    private CardView[] optionCards;
    private FrameLayout[] optionFrames;
    private TextView[] optionTexts;

    private static final String PREFS_NAME = "grow21_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap_game);

        handler = new Handler(Looper.getMainLooper());
        dbHelper = DatabaseHelper.getInstance(this);

        // Check TTS preference
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        ttsEnabled = prefs.getBoolean("voice_instructions", false);
        if (ttsEnabled) {
            tts = new TextToSpeech(this, this);
        }

        // Get category from intent
        category = getIntent().getStringExtra("category");
        if (category == null) category = "vocabulary";

        initViews();
        loadQuestions();
    }

    private void initViews() {
        btnClose = findViewById(R.id.btn_close);
        progressBar = findViewById(R.id.progress_bar);
        ivHeart = findViewById(R.id.iv_heart);
        tvQuestion = findViewById(R.id.tv_question);
        ivQuestionImage = findViewById(R.id.iv_question_image);

        cardOption1 = findViewById(R.id.card_option_1);
        cardOption2 = findViewById(R.id.card_option_2);
        cardOption3 = findViewById(R.id.card_option_3);
        cardOption4 = findViewById(R.id.card_option_4);

        // Get the FrameLayouts inside each CardView
        frameOption1 = (FrameLayout) cardOption1.getChildAt(0);
        frameOption2 = (FrameLayout) cardOption2.getChildAt(0);
        frameOption3 = (FrameLayout) cardOption3.getChildAt(0);
        frameOption4 = (FrameLayout) cardOption4.getChildAt(0);

        tvOption1 = findViewById(R.id.tv_option_1);
        tvOption2 = findViewById(R.id.tv_option_2);
        tvOption3 = findViewById(R.id.tv_option_3);
        tvOption4 = findViewById(R.id.tv_option_4);

        optionCards = new CardView[]{cardOption1, cardOption2, cardOption3, cardOption4};
        optionFrames = new FrameLayout[]{frameOption1, frameOption2, frameOption3, frameOption4};
        optionTexts = new TextView[]{tvOption1, tvOption2, tvOption3, tvOption4};

        btnClose.setOnClickListener(v -> finish());

        // Set click listeners for each card
        for (int i = 0; i < optionCards.length; i++) {
            final int index = i;
            optionCards[i].setOnClickListener(v -> onOptionSelected(index));
        }
    }

    private void loadQuestions() {
        questions = QuestionLoader.loadQuestionsByCategory(this, category);

        // If no questions for this category, load all
        if (questions.isEmpty()) {
            questions = QuestionLoader.loadShuffledQuestions(this);
        }

        if (questions.isEmpty()) {
            Toast.makeText(this, "No questions available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentQuestionIndex = 0;
        score = 0;
        hearts = 3;
        displayQuestion();
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishGame();
            return;
        }

        isAnswered = false;
        QuestionModel question = questions.get(currentQuestionIndex);

        // Set question text
        tvQuestion.setText(question.getQuestion());

        // Set question image
        int imageResId = getResources().getIdentifier(
                question.getImage(), "drawable", getPackageName());
        if (imageResId != 0) {
            ivQuestionImage.setImageResource(imageResId);
        } else {
            ivQuestionImage.setImageResource(R.drawable.bg_placeholder_mascot);
        }

        // Set option texts
        List<String> options = question.getOptions();
        for (int i = 0; i < optionTexts.length && i < options.size(); i++) {
            optionTexts[i].setText(options.get(i));
        }

        // Reset card backgrounds
        for (FrameLayout frame : optionFrames) {
            frame.setBackgroundResource(R.drawable.bg_option_card_default);
        }

        // Enable all cards
        for (CardView card : optionCards) {
            card.setClickable(true);
            card.setEnabled(true);
        }

        // Update progress bar
        int progress = (int) (((float) currentQuestionIndex / questions.size()) * 100);
        progressBar.setProgress(progress);

        // Speak question if TTS enabled
        if (ttsEnabled && tts != null) {
            tts.speak(question.getQuestion(), TextToSpeech.QUEUE_FLUSH, null, "question");
        }
    }

    private void onOptionSelected(int selectedIndex) {
        if (isAnswered) return;
        isAnswered = true;

        // Disable all cards immediately
        for (CardView card : optionCards) {
            card.setClickable(false);
            card.setEnabled(false);
        }

        QuestionModel question = questions.get(currentQuestionIndex);
        String selectedAnswer = optionTexts[selectedIndex].getText().toString();
        String correctAnswer = question.getAnswer();
        boolean isCorrect = selectedAnswer.equals(correctAnswer);

        if (isCorrect) {
            // Correct: green on selected
            optionFrames[selectedIndex].setBackgroundResource(R.drawable.bg_option_card_correct);
            score++;
        } else {
            // Wrong: red on selected, green on correct
            optionFrames[selectedIndex].setBackgroundResource(R.drawable.bg_option_card_wrong);
            hearts--;

            // Find and highlight correct answer
            for (int i = 0; i < optionTexts.length; i++) {
                if (optionTexts[i].getText().toString().equals(correctAnswer)) {
                    optionFrames[i].setBackgroundResource(R.drawable.bg_option_card_correct);
                    break;
                }
            }
        }

        // Save performance
        dbHelper.insertPerformance(question.getId(), question.getCategory(), isCorrect);

        // Check hearts
        if (hearts <= 0) {
            handler.postDelayed(this::showGameOverDialog, 1500);
        } else {
            handler.postDelayed(() -> showRewardDialog(isCorrect, correctAnswer), 1500);
        }
    }

    private void showRewardDialog(boolean isCorrect, String correctAnswer) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reward, null);

        TextView tvEmoji = dialogView.findViewById(R.id.tv_reward_emoji);
        TextView tvTitle = dialogView.findViewById(R.id.tv_reward_title);
        TextView tvMessage = dialogView.findViewById(R.id.tv_reward_message);
        Button btnNext = dialogView.findViewById(R.id.btn_next);

        if (isCorrect) {
            tvEmoji.setText("⭐");
            tvTitle.setText(R.string.reward_correct_title);
            tvMessage.setText(R.string.reward_correct_message);
        } else {
            tvEmoji.setText("💪");
            tvTitle.setText(R.string.reward_wrong_title);
            tvMessage.setText(getString(R.string.reward_wrong_message_format, correctAnswer));
        }

        // Check if last question
        boolean isLast = (currentQuestionIndex >= questions.size() - 1);
        btnNext.setText(isLast ? R.string.btn_finish : R.string.btn_next);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            if (isLast) {
                finishGame();
            } else {
                loadNextQuestion();
            }
        });

        dialog.show();
    }

    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.game_over_title);
        builder.setMessage(R.string.game_over_message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.btn_try_again, (dialog, which) -> {
            dialog.dismiss();
            loadQuestions();
        });
        builder.setNegativeButton(R.string.btn_go_back, (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }

    private void loadNextQuestion() {
        currentQuestionIndex++;
        displayQuestion();
    }

    private void finishGame() {
        // Save session
        dbHelper.insertSession(category, score, questions.size());

        // Show completion dialog
        String message = getString(R.string.session_complete_format, score, questions.size());
        new AlertDialog.Builder(this)
                .setTitle(R.string.session_complete_title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_go_back, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.US);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
