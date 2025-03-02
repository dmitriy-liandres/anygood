package helpers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ApplicationHelper {

    //language code->language name
    private static final Map<String, String> languages;
    //we use a separate list with languages to show them in the proper order in settings
    private static final List<String> languageFullNames;

    static {
        languageFullNames = Arrays.asList("English", "Русский", "עברית");
        languages = new HashMap<>();
        languages.put("en", "English");
        languages.put("ru", "Русский");
        languages.put("il", "עברית");

    }

    public static List<String> getLanguagesFullNames() {
        return languageFullNames;
    }

    public static String getLanguageCodeByName(String languageName) {
        return languages.entrySet().stream().filter(l -> l.getValue().equalsIgnoreCase(languageName)).findFirst().get().getKey();
    }

    public static String getUserLanguageCode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String LanguageCode = prefs.getString("selectedLanguageCode", null);
        if (LanguageCode == null) {
            LanguageCode = Locale.getDefault().getLanguage().toLowerCase(); // Returns language code, e.g., "en"
        }
        return LanguageCode;
    }

    public static String getUserLanguageFullName(Context context) {
        String LanguageCode = getUserLanguageCode(context);
        return languages.get(LanguageCode);
    }


    public static String getUserCountryCode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String selectedCountryCode = prefs.getString("selectedCountryCode", null);
        if (selectedCountryCode == null) {
            selectedCountryCode = Locale.getDefault().getCountry().toLowerCase();
        }
        return selectedCountryCode;
    }

    public static String getUserCountryFullName(Context context) {
        String languageCode = Locale.getDefault().getLanguage().toLowerCase();
        return CountryMapGenerator.getCountryMap(context, languageCode).get(getUserCountryCode(context));
    }


    public static void updateLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode, Locale.getDefault().getCountry());
        Locale.setDefault(locale);

        Resources res = context.getResources();
        Configuration config = res.getConfiguration();

        config.setLocale(locale);
        config.setLocales(new LocaleList(locale));
        LocaleList.setDefault(new LocaleList(locale));


        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
