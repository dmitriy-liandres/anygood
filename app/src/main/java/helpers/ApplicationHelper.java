package helpers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

import java.util.Locale;

public class ApplicationHelper {


    public static String getUserLanguage(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedLanguage = prefs.getString("selectedLanguage", null);
        if (savedLanguage == null) {
            String LanguageCode = Locale.getDefault().getLanguage().toLowerCase(); // Returns language code, e.g., "en"
            switch (LanguageCode) {
                case "ru":
                    return "Русский";
                case "he":
                    return "עברית";
                default:
                    return "English";
            }
        } else {
            return savedLanguage;
        }
    }

    public static String getUserCountry(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedCountry = prefs.getString("selectedCountry", null);
        if (savedCountry == null) {
            String LanguageCode = Locale.getDefault().getCountry().toLowerCase();
            switch (LanguageCode) {
                case "ru":
                    return "Россия";
                case "he":
                    return "ישראל";
                default:
                    return "United States";
            }
        } else {
            return savedCountry;
        }
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
