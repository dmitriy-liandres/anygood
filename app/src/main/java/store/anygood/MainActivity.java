package store.anygood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import store.anygood.model.Product;
import store.anygood.model.Question;
import store.anygood.model.QuestionWithAnswer;


public class MainActivity extends AppCompatActivity {

    // UI elements for the initial query
    private TextInputEditText editTextQuery;
    private MaterialButton buttonStart;
    // Questionnaire UI elements
    private LinearLayout layoutQuestions;
    private TextView textQuestion;
    private LinearLayout checkboxContainer;
    private TextInputLayout freeTextInputLayout;
    private TextInputEditText editTextFree;
    private MaterialButton buttonNext;

    // Results UI elements
    private LinearLayout layoutResults;

    // Progress indicator
    private ProgressBar progressBar;

    // Data for questionnaire
    private List<QuestionWithAnswer> questionsWithAnswers = new ArrayList<>();

    private static final int TOTAL_QUESTIONS = 10; // from ChatGPT
    private String initialQuery; // The user's initial query

    private boolean isAdditionalInfoPhase = false; // after 5 ChatGPT questions

    private static final int REQUEST_SETTINGS = 1001; // or any unique integer

    private static final ChatGPTClient chatGPTClient = new ChatGPTClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1) Load user’s stored language
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedLanguage = prefs.getString("selectedLanguage", "English");

        // 2) Update the locale *before* calling super.onCreate
        updateLocale(savedLanguage);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initial query views
        editTextQuery = findViewById(R.id.editTextQuery);
        buttonStart = findViewById(R.id.buttonStart);

        // Questionnaire views
        layoutQuestions = findViewById(R.id.layoutQuestions);
        textQuestion = findViewById(R.id.textQuestion);
        checkboxContainer = findViewById(R.id.checkboxContainer);
        freeTextInputLayout = findViewById(R.id.freeTextInputLayout);
        editTextFree = findViewById(R.id.editTextFree);
        buttonNext = findViewById(R.id.buttonNext);

        // Results layout
        layoutResults = findViewById(R.id.layoutResults);

        // Progress bar
        progressBar = findViewById(R.id.progressBar);

        buttonStart.setOnClickListener(v -> {
            // Collect the user’s initial query
            initialQuery = editTextQuery.getText().toString().trim();
            if (initialQuery.isEmpty()) {
                Toast.makeText(MainActivity.this, getString(R.string.what_product_hint), Toast.LENGTH_SHORT).show();
                return;
            }
            // Hide initial input UI, show question UI, etc.
            findViewById(R.id.initialInputLayout).setVisibility(View.GONE);
            buttonStart.setVisibility(View.GONE);
            layoutQuestions.setVisibility(View.VISIBLE);


            chatGPTClient.login("null", "null", new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this, getString(R.string.error_general), Toast.LENGTH_LONG).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        fetchNextQuestion();
                    });
                }
            });


        });

        buttonNext.setOnClickListener(v -> {
            if (isAdditionalInfoPhase) {
                // This is the final hard-coded question
                String additionalInfo = editTextFree.getText().toString().trim();
                if (additionalInfo.length() > 100) {
                    Toast.makeText(this, getString(R.string.max_hundred_chars), Toast.LENGTH_SHORT).show();
                    return;
                }
                // Now get recommendations or finish
                finishQuestionnaire();
                return;
            }


            // Otherwise, user is answering a ChatGPT question
            List<String> answers = getUserAnswer();
            if (answers.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_or_type), Toast.LENGTH_SHORT).show();
                return;
            }

            QuestionWithAnswer questionWithAnswer = questionsWithAnswers.get(questionsWithAnswers.size() - 1);
            questionWithAnswer.setAnswers(answers);

            // Append question & answer to conversation

            if (!questionWithAnswer.getQuestion().isLast() && questionsWithAnswers.size() < TOTAL_QUESTIONS) {
                // Request the next question from ChatGPT
                fetchNextQuestion();
            } else {
                // We have all ChatGPT questions, show final "additional info" question
                showAdditionalInfoQuestion();
            }
        });
    }

    private void updateLocale(String language) {
        // same approach: map "Russian" -> "ru" etc.
        String languageCode = SettingsActivity.mapLanguageNameToCode(language);

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);

        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    private void fetchNextQuestion() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuestions.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedLanguage = prefs.getString("selectedLanguage", "English");
        String savedCountry = prefs.getString("selectedCountry", "United States");


        chatGPTClient.generateNextQuestion(initialQuery,
                savedCountry,
                savedLanguage,
                questionsWithAnswers,
                new ChatGPTClient.NextQuestionCallback() {
                    @Override
                    public void onQuestionReceived(Question question) {
                        runOnUiThread(() -> {
                            // Show question in UI
                            if (question.isLast()) {
                                showAdditionalInfoQuestion();
                            } else {
                                questionsWithAnswers.add(new QuestionWithAnswer(question));
                                showCurrentQuestion();
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show()
                        );
                    }
                });
    }


    private void showCurrentQuestion() {
        // Clear UI
        checkboxContainer.removeAllViews();
        checkboxContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);



        layoutQuestions.setVisibility(View.VISIBLE);

        Question currentQuestion = questionsWithAnswers.get(questionsWithAnswers.size() - 1).getQuestion();

        textQuestion.setText(currentQuestion.getText());
        editTextFree.setText("");

        if (!currentQuestion.getOptions().isEmpty()) {
            // Add each option
            for (String opt : currentQuestion.getOptions()) {
                CheckBox cb = new CheckBox(this);
                cb.setText(opt);
                checkboxContainer.addView(cb);
            }
            // Show or hide free texteditTextFree.setVisibility(View.VISIBLE);
        }
        // If no options => free text only
        if (currentQuestion.isAllowFreeText()) {
            freeTextInputLayout.setVisibility(View.VISIBLE);
            editTextFree.setVisibility(View.VISIBLE);
        } else {
            freeTextInputLayout.setVisibility(View.GONE);
            editTextFree.setVisibility(View.GONE);
        }

    }

    private List<String> getUserAnswer() {
        List<String> selectedOptions = new ArrayList<>();

        int childCount = checkboxContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = checkboxContainer.getChildAt(i);
            if (child instanceof CheckBox) {
                CheckBox cb = (CheckBox) child;
                if (cb.isChecked()) {
                    selectedOptions.add(cb.getText().toString());
                }
            }
        }

        if (editTextFree.getVisibility() == View.VISIBLE ) {
            String text = editTextFree.getText().toString().trim();
            if(text.length() > 0) {
                selectedOptions.add(editTextFree.getText().toString().trim());
            }
        }
        return selectedOptions;
    }

    private void showAdditionalInfoQuestion() {
        // Mark that we are now in the final phase
        isAdditionalInfoPhase = true;

        // Clear UI
        checkboxContainer.removeAllViews();
        checkboxContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        textQuestion.setText(getString(R.string.additional_info_question));
        editTextFree.setText("");

        layoutQuestions.setVisibility(View.VISIBLE);

        freeTextInputLayout.setVisibility(View.VISIBLE);
        editTextFree.setVisibility(View.VISIBLE);
    }

    private void finishQuestionnaire() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuestions.setVisibility(View.GONE);

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedLanguage = prefs.getString("selectedLanguage", "English");
        String savedCountry = prefs.getString("selectedCountry", "United States");

        chatGPTClient.getProductRecommendations(initialQuery,
                savedCountry,
                savedLanguage,
                questionsWithAnswers,
                editTextFree.getText().toString(),
                new ChatGPTClient.RecommendationsCallback() {
                    @Override
                    public void onRecommendationsReceived(List<Product> recommendedProducts) {
                        // show recommendations
                        runOnUiThread(() -> showProductRecommendations(recommendedProducts));
                    }

                    @Override
                    public void onError(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show()
                        );
                    }
                });
    }

    private void showProductRecommendations(List<Product> products) {
        layoutQuestions.setVisibility(View.GONE);
        layoutResults.removeAllViews();
        layoutResults.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        for (Product product : products) {
            // Wrap your Activity context with the custom card style
            Context cardContext = new ContextThemeWrapper(this, R.style.ModernCardStyle);

            // Create the MaterialCardView using 2-arg or 3-arg constructor
            MaterialCardView card = new MaterialCardView(cardContext);

            // Optionally set LayoutParams if needed
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 8, 0, 8);
            card.setLayoutParams(cardParams);

            // Add an internal layout for content
            LinearLayout cardLayout = new LinearLayout(cardContext);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(24, 24, 24, 24);

            // Product name
            TextView productName = new TextView(cardContext);
            productName.setText(product.getName());
            productName.setTextSize(16f);
            productName.setTextColor(getResources().getColor(R.color.colorOnSurface));
            cardLayout.addView(productName);

            // “Buy” button
            MaterialButton btnBuy = new MaterialButton(cardContext);
            btnBuy.setText(getString(R.string.buy));
            btnBuy.setOnClickListener(v -> {
                // Use your own logic to open product link
                String url = AmazonApiClient.generateAmazonLink(product.getKeyword());
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            });
            cardLayout.addView(btnBuy);

            // Add cardLayout to the card
            card.addView(cardLayout);
            // Add the card to layoutResults
            layoutResults.addView(card);
        }

        // Add a “Start Again” button at the end
        MaterialButton btnRestart = new MaterialButton(this);
        btnRestart.setText(getString(R.string.start_again));
        btnRestart.setOnClickListener(v -> resetUI());
        layoutResults.addView(btnRestart);
    }


    private void resetUI() {
        questionsWithAnswers = new ArrayList<>();
        layoutResults.setVisibility(View.GONE);
        layoutQuestions.setVisibility(View.GONE);
        findViewById(R.id.initialInputLayout).setVisibility(View.VISIBLE);
        buttonStart.setVisibility(View.VISIBLE);
        editTextQuery.setText("");
        isAdditionalInfoPhase = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            // Open the new SettingsActivity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_SETTINGS);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS && resultCode == Activity.RESULT_OK) {
            // The user changed the language
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String savedLanguage = prefs.getString("selectedLanguage", "English");
            updateLocale(savedLanguage);
            recreate(); // Now this re-creates MainActivity in the new language
        }
    }
}
