package store.anygood;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI elements for the initial query
    private TextInputEditText editTextQuery;
    private MaterialButton buttonStart;
    private Spinner spinnerLanguage;
    private Spinner spinnerCountry;

    // Questionnaire UI elements
    private LinearLayout layoutQuestions;
    private TextView textQuestion;
    private RadioGroup radioGroup;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Locale is applied from resources.
        setContentView(R.layout.activity_main);

        // Initial query views
        editTextQuery = findViewById(R.id.editTextQuery);
        buttonStart = findViewById(R.id.buttonStart);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerCountry = findViewById(R.id.spinnerCountry);

        // Populate language spinner from resource arrays
        String[] languages = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(languageAdapter);

        // Set listener for language spinner to update the locale dynamically
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstCall = true; // avoid triggering on initial setup

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = (String) parent.getItemAtPosition(position);
                String languageCode = getLanguageCode(selectedLanguage);
                if (!languageCode.equals(getCurrentLanguageCode())) {
                    if (!isFirstCall) {
                        updateLocale(languageCode);
                    }
                }
                isFirstCall = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Populate country spinner from resource arrays
        String[] countries = getResources().getStringArray(R.array.countries);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        // Questionnaire views
        layoutQuestions = findViewById(R.id.layoutQuestions);
        textQuestion = findViewById(R.id.textQuestion);
        radioGroup = findViewById(R.id.radioGroupOptions);
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


            // Now fetch the first question from ChatGPT
            fetchNextQuestion();  // calls generateNextQuestion or similar
        });

        buttonNext.setOnClickListener(v -> {
            if (isAdditionalInfoPhase) {
                // This is the final hard-coded question
                String additionalInfo = editTextFree.getText().toString().trim();
                if (additionalInfo.length() > 100) {
                    Toast.makeText(this, "Max 100 characters allowed!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Now get recommendations or finish
                finishQuestionnaire();
                return;
            }


            // Otherwise, user is answering a ChatGPT question
            String answer = getUserAnswer();
            if (answer.isEmpty()) {
                Toast.makeText(this, getString(R.string.please_select_or_type), Toast.LENGTH_SHORT).show();
                return;
            }

            QuestionWithAnswer questionWithAnswer = questionsWithAnswers.get(questionsWithAnswers.size() - 1);
            questionWithAnswer.setAnswer(answer);

            // Append question & answer to conversation

            if (!questionWithAnswer.getQuestion().isLast() && questionsWithAnswers.size() < TOTAL_QUESTIONS) {
                // Request the next question from ChatGPT
                fetchNextQuestion();
            } else {
                // We have all ChatGPT questions, show final "additional info" question
                showAdditionalInfoQuestion();
            }
        });
        editTextFree.setOnTouchListener((v, event) -> {
            radioGroup.clearCheck();
            return false;
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> editTextFree.setText(""));

    }

    private void fetchNextQuestion() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuestions.setVisibility(View.GONE);
        ChatGPTClient.generateNextQuestion(initialQuery,
                spinnerCountry.getSelectedItem().toString(),
                spinnerLanguage.getSelectedItem().toString(),
                questionsWithAnswers,
                new ChatGPTClient.NextQuestionCallback() {
                    @Override
                    public void onQuestionReceived(Question question) {
                        runOnUiThread(() -> {
                            // Show question in UI
                            questionsWithAnswers.add(new QuestionWithAnswer(question));
                            showCurrentQuestion();
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
        radioGroup.removeAllViews();
        editTextFree.setText("");
        progressBar.setVisibility(View.GONE);
        radioGroup.setVisibility(View.VISIBLE);
        layoutQuestions.setVisibility(View.VISIBLE);

        Question currentQuestion = questionsWithAnswers.get(questionsWithAnswers.size() - 1).getQuestion();

        textQuestion.setText(currentQuestion.getText());
        if (!currentQuestion.getOptions().isEmpty()) {
            // Add each option
            for (String opt : currentQuestion.getOptions()) {
                RadioButton rb = new RadioButton(this);
                rb.setText(opt);
                radioGroup.addView(rb);
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

    private String getUserAnswer() {
        // If there's a checked radio, use that
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId != -1) {
            RadioButton rb = findViewById(checkedId);
            return rb.getText().toString();
        }
        // Else, if free text is visible, use that
        if (editTextFree.getVisibility() == View.VISIBLE) {
            return editTextFree.getText().toString().trim();
        }
        return "";
    }

    private void showAdditionalInfoQuestion() {
        // Mark that we are now in the final phase
        isAdditionalInfoPhase = true;

        // Clear UI
        radioGroup.removeAllViews();
        radioGroup.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        textQuestion.setText("Provide any additional info (max 100 characters)");
        editTextFree.setText("");
        freeTextInputLayout.setVisibility(View.VISIBLE);
        editTextFree.setVisibility(View.VISIBLE);
    }

    // Helper: Map language names to locale codes.
    private String getLanguageCode(String language) {
        switch (language) {
            case "Russian":
                return "ru";
            case "Hebrew":
                return "he";
            case "Arabic":
                return "ar";
            case "German":
                return "de";
            case "French":
                return "fr";
            case "English":
            default:
                return "en";
        }
    }

    // Helper: Get the current language code.
    private String getCurrentLanguageCode() {
        return getResources().getConfiguration().locale.getLanguage();
    }

    // Update the locale and recreate the activity.
    private void updateLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
        recreate();
    }

    private void finishQuestionnaire() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuestions.setVisibility(View.GONE);
        ChatGPTClient.getProductRecommendations(initialQuery,
                spinnerCountry.getSelectedItem().toString(),
                spinnerLanguage.getSelectedItem().toString(),
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
}
