package store.anygood;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Map;

import helpers.ApplicationHelper;
import helpers.CountryMapGenerator;

public class SettingsActivity extends AppCompatActivity {
    private AutoCompleteTextView dropdownLanguage, dropdownCountry;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1) Load user’s stored language
        String savedLanguageCode = ApplicationHelper.getUserLanguageCode(this);

        // 2) Update the locale *before* calling super.onCreate
        ApplicationHelper.updateLocale(this, savedLanguageCode);


        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_settings);

        dropdownLanguage = findViewById(R.id.dropdownLanguage);
        dropdownCountry = findViewById(R.id.dropdownCountry);
        buttonSave = findViewById(R.id.buttonSave);

        // Setup spinners
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, ApplicationHelper.getLanguagesFullNames());
        dropdownLanguage.setAdapter(languageAdapter);
        dropdownLanguage.setThreshold(1);

        Map<String, String> countriesMap = CountryMapGenerator.getCountryMap(this, ApplicationHelper.getUserLanguageCode(this));
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, new ArrayList<>(countriesMap.values()));
        dropdownCountry.setAdapter(countryAdapter);
        dropdownCountry.setThreshold(1);

        // Ensure dropdown opens when clicked
        dropdownLanguage.setOnClickListener(v -> dropdownLanguage.showDropDown());
        dropdownLanguage.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLangName = (String) parent.getItemAtPosition(position);

            String languageCode = ApplicationHelper.getLanguageCodeByName(selectedLangName);
            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putString("selectedLanguageCode", languageCode);

            editor.apply();

            ApplicationHelper.updateLocale(SettingsActivity.this, languageCode);

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        });
        dropdownCountry.setOnClickListener(v -> dropdownCountry.showDropDown());
        dropdownCountry.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCountryName = (String) parent.getItemAtPosition(position);

            String languageCode = ApplicationHelper.getUserCountryCode(this);
            String countryCode = CountryMapGenerator.getCountryCodeByName(this, languageCode, selectedCountryName);

            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putString("selectedCountryCode", countryCode);
            editor.apply();
        });


        // Load saved prefs
        String savedLanguage = ApplicationHelper.getUserLanguageFullName(this);
        String savedCountry = ApplicationHelper.getUserCountryFullName(this);

        dropdownLanguage.setText(savedLanguage, false);
        dropdownCountry.setText(savedCountry, false);


        buttonSave.setOnClickListener(v -> {
            setResult(Activity.RESULT_OK);
            finish();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Manually trigger back press

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
