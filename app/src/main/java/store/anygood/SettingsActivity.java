package store.anygood;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import helpers.ApplicationHelper;
import helpers.CountryMapGenerator;

public class SettingsActivity extends AppCompatActivity {
    private AutoCompleteTextView dropdownLanguage, dropdownCountry ;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dropdownLanguage = findViewById(R.id.dropdownLanguage);
        dropdownCountry = findViewById(R.id.dropdownCountry);
        buttonSave = findViewById(R.id.buttonSave);

        // Setup spinners
        String[] languages = {"English", "Русский", "עברית"};
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, languages);
        dropdownLanguage.setAdapter(languageAdapter);

        Map<String, String> countriesMap = CountryMapGenerator.getCountryMap();
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, new ArrayList<>(countriesMap.values()));
        dropdownCountry.setAdapter(countryAdapter);

        // Ensure dropdown opens when clicked
        dropdownLanguage.setOnClickListener(v -> dropdownLanguage.showDropDown());
        dropdownCountry.setOnClickListener(v -> dropdownCountry.showDropDown());


        // Load saved prefs
        String savedLanguage = ApplicationHelper.getUserLanguage(this);
        String savedCountry = ApplicationHelper.getUserCountry(this);

        dropdownLanguage.setText(savedLanguage, false);
        dropdownCountry.setText(savedCountry, false);


        buttonSave.setOnClickListener(v -> {
            // Save selected values in SharedPreferences
            String selectedLang = dropdownLanguage.getText().toString();
            String selectedCountry = dropdownCountry.getText().toString();

            SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
            editor.putString("selectedLanguage", selectedLang);
            editor.putString("selectedCountry", selectedCountry);
            editor.apply();

            // Optionally show a toast
            // Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            // Close the SettingsActivity

            // Now update the locale for immediate language switch:
            updateLocale(selectedLang);

            // Then finish this activity (or go back to Main)
            setResult(Activity.RESULT_OK);
            finish();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Manually trigger back press

    }

    private void updateLocale(String language) {
        // Map "English" -> "en", "Russian" -> "ru", etc.
        String languageCode = mapLanguageNameToCode(language);


        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);

        // Update the app configuration
        res.updateConfiguration(config, res.getDisplayMetrics());

        // Recreate *this* activity to reflect the new locale
        //recreate();
    }

    // Example mapping function
    public static String mapLanguageNameToCode(String languageName) {
        switch (languageName) {
            case "Русский":
                return "ru";
            case "עברית":
                return "il";
            default:
                return "en"; // English fallback
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
