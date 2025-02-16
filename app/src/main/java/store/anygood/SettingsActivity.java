package store.anygood;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private Spinner spinnerLanguage, spinnerCountry;
    private Button buttonSave;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        buttonSave = findViewById(R.id.buttonSave);

        // Setup spinners
        String[] languages = {"English", "Русский", "עברית"};
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(langAdapter);

        String[] countries = {"United States", "Россия", "ישראל"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryAdapter);

        // Load saved prefs
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedLanguage = prefs.getString("selectedLanguage", "English");
        String savedCountry = prefs.getString("selectedCountry", "United States");

        // Set the spinner positions to match saved preferences
        int langPos = langAdapter.getPosition(savedLanguage);
        spinnerLanguage.setSelection(langPos);

        int countryPos = countryAdapter.getPosition(savedCountry);
        spinnerCountry.setSelection(countryPos);

        buttonSave.setOnClickListener(v -> {
            // Save selected values in SharedPreferences
            String selectedLang = (String) spinnerLanguage.getSelectedItem();
            String selectedCtry = (String) spinnerCountry.getSelectedItem();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("selectedLanguage", selectedLang);
            editor.putString("selectedCountry", selectedCtry);
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
                return "he";
            case "Arabic":
                return "ar";
            case "German":
                return "de";
            case "French":
                return "fr";
            default:
                return "en"; // English fallback
        }
    }

}
