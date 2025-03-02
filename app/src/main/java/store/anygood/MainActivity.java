package store.anygood;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Space;
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

import helpers.ApplicationHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import store.anygood.model.dto.ProductDTO;
import store.anygood.model.dto.QuestionDTO;
import store.anygood.model.dto.QuestionWithAnswerDTO;
import store.anygood.model.dto.TelephoneInfoDTO;


public class MainActivity extends AppCompatActivity {

    // UI elements for the initial query
    private TextInputEditText editTextQuery;
    private MaterialButton buttonStart;
    // Questionnaire UI elements
    private MaterialCardView layoutQuestionsCard;
    private TextView textQuestion;
    private LinearLayout checkboxContainer;
    private TextInputLayout freeTextInputLayout;
    private TextInputEditText editTextFree;
    private MaterialButton buttonNext;

    // Results UI elements
    private LinearLayout layoutResults;

    // Progress indicator
    private ProgressBar progressBar;

    private static final int TOTAL_QUESTIONS = 10; // from ChatGPT
    private String initialQuery; // The user's initial query

    private boolean isAdditionalInfoPhase = false; // after 5 ChatGPT questions

    private QuestionDTO currentQuestionDTO;

    private List<ProductDTO> products = new ArrayList<>();

    private String searchSessionId = "";

    private static final int REQUEST_SETTINGS = 1001; // or any unique integer

    private static final ChatGPTClient chatGPTClient = new ChatGPTClient();

    private MaterialButton btnLoadMore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1) Load user’s stored language
        String savedLanguageCode = ApplicationHelper.getUserLanguageCode(this);

        // 2) Update the locale *before* calling super.onCreate
        ApplicationHelper.updateLocale(this, savedLanguageCode);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle( getString(R.string.app_name));

        // Initial query views
        editTextQuery = findViewById(R.id.editTextQuery);
        buttonStart = findViewById(R.id.buttonStart);

        // Questionnaire views
        layoutQuestionsCard = findViewById(R.id.layoutQuestionsCard);
        textQuestion = findViewById(R.id.textQuestion);
        checkboxContainer = findViewById(R.id.checkboxContainer);
        freeTextInputLayout = findViewById(R.id.freeTextInputLayout);
        editTextFree = findViewById(R.id.editTextFree);
        buttonNext = findViewById(R.id.buttonNext);

        // Results layout
        layoutResults = findViewById(R.id.layoutResults);

        // Progress bars
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
            progressBar.setVisibility(View.VISIBLE);
            layoutQuestionsCard.setVisibility(View.GONE);

            TelephoneInfoDTO telephoneInfo = new TelephoneInfoDTO(ApplicationHelper.getAndroidID(this),
                    ApplicationHelper.getUserCountryCode(this),
                    ApplicationHelper.getUserLanguageCode(this)
            );

            chatGPTClient.login(telephoneInfo, "test", "test", new Callback() {
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
            // Append question & answer to conversation
            if (!currentQuestionDTO.isLast()) {
                // Request the next question from ChatGPT
                fetchNextQuestion();
            } else {
                // We have all ChatGPT questions, show final "additional info" question
                showAdditionalInfoQuestion();
            }
        });
    }

    private void fetchNextQuestion() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuestionsCard.setVisibility(View.GONE);

        String savedLanguage = ApplicationHelper.getUserLanguageFullName(this);
        String savedCountry = ApplicationHelper.getUserCountryFullName(this);

        QuestionWithAnswerDTO questionWithAnswerDTO = new QuestionWithAnswerDTO();
        questionWithAnswerDTO.setQuestionId(currentQuestionDTO == null ? null : currentQuestionDTO.getQuestionId());
        questionWithAnswerDTO.setAnswers(getUserAnswer());
        chatGPTClient.generateNextQuestion(searchSessionId,
                initialQuery,
                savedCountry,
                savedLanguage,
                questionWithAnswerDTO,
                new ChatGPTClient.NextQuestionCallback() {
                    @Override
                    public void onQuestionReceived(String searchSessionId, QuestionDTO question) {
                        runOnUiThread(() -> {
                            MainActivity.this.searchSessionId = searchSessionId;
                            // Show question in UI
                            if (question.isLast()) {
                                showAdditionalInfoQuestion();
                            } else {
                                showCurrentQuestion(question);
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


    private void showCurrentQuestion(QuestionDTO question) {
        this.currentQuestionDTO = question;
        // Clear UI
        checkboxContainer.removeAllViews();
        checkboxContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);


        layoutQuestionsCard.setVisibility(View.VISIBLE);

        textQuestion.setText(question.getText());
        editTextFree.setText("");

        if (!question.getOptions().isEmpty()) {
            // Add each option
            for (String opt : question.getOptions()) {
                CheckBox cb = new CheckBox(this);
                cb.setText(opt);
                checkboxContainer.addView(cb);
            }
            // Show or hide free texteditTextFree.setVisibility(View.VISIBLE);
        }
        // If no options => free text only
        if (question.isAllowFreeText()) {
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

        if (editTextFree.getVisibility() == View.VISIBLE) {
            String text = editTextFree.getText().toString().trim();
            if (text.length() > 0) {
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

        layoutQuestionsCard.setVisibility(View.VISIBLE);

        freeTextInputLayout.setVisibility(View.VISIBLE);
        editTextFree.setVisibility(View.VISIBLE);
    }

    private void finishQuestionnaire() {
        progressBar.setVisibility(View.VISIBLE);
        layoutQuestionsCard.setVisibility(View.GONE);

        chatGPTClient.getProductRecommendations(
                searchSessionId,
                editTextFree.getText().toString(),
                new ChatGPTClient.RecommendationsCallback() {
                    @Override
                    public void onRecommendationsReceived(List<ProductDTO> recommendedProducts) {
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

    private void showProductRecommendations(List<ProductDTO> products) {
        layoutQuestionsCard.setVisibility(View.GONE);
        layoutResults.removeAllViews();
        layoutResults.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        this.products.addAll(products);

        for (ProductDTO product : this.products) {
            Context cardContext = new ContextThemeWrapper(this, R.style.ModernCardStyle);
            MaterialCardView card = new MaterialCardView(cardContext);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, 16, 0, 16);
            card.setLayoutParams(cardParams);
            card.setCardElevation(4f);
            card.setRadius(12f);

            LinearLayout cardLayout = new LinearLayout(cardContext);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            cardLayout.setPadding(32, 32, 32, 32);

            TextView productName = new TextView(cardContext);
            productName.setText(product.getName());
            productName.setTextSize(18f);
            productName.setTypeface(null, Typeface.BOLD);
            productName.setTextColor(getResources().getColor(R.color.colorOnSurface));
            cardLayout.addView(productName);

            // Add space between product name and description
            Space space = new Space(cardContext);
            LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    16
            );
            space.setLayoutParams(spaceParams);
            cardLayout.addView(space);

            TextView productDescription = new TextView(cardContext);
            productDescription.setText(product.getDescription());
            productDescription.setTextSize(16f);
            productDescription.setLineSpacing(2f, 1.2f);
            productDescription.setTextColor(getResources().getColor(R.color.colorOnSurface));
            cardLayout.addView(productDescription);

            MaterialButton btnFind = new MaterialButton(cardContext);
            btnFind.setText(getString(R.string.find));
            btnFind.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSuccess)));
            btnFind.setCornerRadius(24);
            btnFind.setOnClickListener(v -> {
                String url = product.getLink();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            });
            cardLayout.addView(btnFind);

            card.addView(cardLayout);
            layoutResults.addView(card);
        }

        FrameLayout btnContainer = new FrameLayout(this);
        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        btnContainer.setLayoutParams(containerParams);

        //if the button hasn't been created, let's create it
        btnLoadMore = new MaterialButton(this);
        btnLoadMore.setText(getString(R.string.load_more));
        btnLoadMore.setTextSize(12f);
        btnLoadMore.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ProgressBar progressLoader = new ProgressBar(this);
        FrameLayout.LayoutParams loaderParams = new FrameLayout.LayoutParams(
                80, 80, Gravity.CENTER // Center the loader over the button
        );
        progressLoader.setLayoutParams(loaderParams);


        if (this.products.size() < 10) {
            btnLoadMore.setEnabled(true);
            btnLoadMore.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            btnLoadMore.setOnClickListener(v -> loadMoreRecommendations());
            progressLoader.setVisibility(View.VISIBLE);
        } else {
            btnLoadMore.setEnabled(false);
            btnLoadMore.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDisabled)));
            progressLoader.setVisibility(View.GONE);
        }

        // Ensure the ProgressBar is drawn on top
        btnContainer.addView(btnLoadMore);  // Add button first (background)
        btnContainer.addView(progressLoader); // Add loader second (foreground)

// Add the FrameLayout to layoutResults
        layoutResults.addView(btnContainer);


        MaterialButton btnStartOver = new MaterialButton(this);
        btnStartOver.setText(getString(R.string.start_again));
        btnStartOver.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSecondaryVariant)));
        btnStartOver.setOnClickListener(v -> resetUI());
        layoutResults.addView(btnStartOver);

    }

    private void loadMoreRecommendations() {
        disableLoadMore();

        chatGPTClient.getProductRecommendationsAdditional(searchSessionId,
                new ChatGPTClient.RecommendationsCallback() {
                    @Override
                    public void onRecommendationsReceived(List<ProductDTO> recommendedProducts) {
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

    private void disableLoadMore() {
        btnLoadMore.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDisabled)));
        btnLoadMore.setEnabled(false); // Disable button to prevent multiple clicks
    }


    private void resetUI() {
        currentQuestionDTO = null;
        this.products = new ArrayList<>();
        searchSessionId = "";
        layoutResults.setVisibility(View.GONE);
        layoutQuestionsCard.setVisibility(View.GONE);
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
        if (requestCode == REQUEST_SETTINGS && (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED)) {
            // The user changed the language
            //String savedLanguageCode = ApplicationHelper.getUserLanguageCode(this);
            //ApplicationHelper.updateLocale(this, savedLanguageCode);
            recreate(); // Now this re-creates MainActivity in the new language
        }
    }


}
