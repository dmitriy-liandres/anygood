package helpers;

import android.content.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryMapGenerator {
    //language->country code>country name
    private static Map<String, Map<String, String>> countryMap = new HashMap<>();

    private static final List<String> countryCodes = Arrays.asList("af", "al", "dz", "ad", "ao", "ar", "am", "au", "at", "az", "bh", "bd", "by", "be", "bz", "bj", "bt", "bo", "ba", "bw", "br", "bn", "bg", "bf", "bi", "kh", "cm", "ca", "cv", "cf", "td", "cl", "cn", "co", "km", "cg", "cd", "cr", "hr", "cu", "cy", "cz", "dk", "dj", "do", "ec", "eg", "sv", "gq", "er", "ee", "et", "fj", "fi", "fr", "ga", "gm", "ge", "de", "gh", "gr", "gt", "gn", "gy", "ht", "hn", "hu", "is", "in", "id", "ir", "iq", "ie", "il", "it", "jm", "jp", "jo", "kz", "ke", "kw", "kg", "la", "lv", "lb", "ls", "lr", "ly", "li", "lt", "lu", "mg", "mw", "my", "mv", "ml", "mt", "mr", "mu", "mx", "mc", "mn", "me", "ma", "mz", "mm", "na", "np", "nl", "nz", "ni", "ne", "ng", "no", "om", "pk", "pa", "py", "pe", "ph", "pl", "pt", "qa", "ro", "ru", "rw", "sa", "sn", "rs", "sg", "sk", "si", "so", "za", "kr", "es", "lk", "sd", "se", "ch", "sy", "tw", "tj", "tz", "th", "tg", "tn", "tr", "tm", "ug", "ua", "ae", "gb", "us", "uy", "uz", "ve", "vn", "ye", "zm", "zw");

    public static String getCountryCodeByName(Context context, String langCode, String countryName) {
        Map<String, String> countries = getCountryMap(context, langCode);
        return countries.entrySet().stream().filter(c->c.getValue().equalsIgnoreCase(countryName)).findFirst().get().getKey();
    }
    public static Map<String, String> getCountryMap(Context context, String langCode) {
        Map<String, String> countries = countryMap.get(langCode);
        if (countries == null) {
            countries = new HashMap<>();
            for (String countryCode : countryCodes) {
                String keyName = "country_" + countryCode; // Dynamic key name
                int resId = context.getResources().getIdentifier(keyName, "string", context.getPackageName());

                if (resId != 0) { // Ensure the resource exists
                    String localizedText = context.getString(resId);
                    countries.put(countryCode, localizedText);
                }
            }
            countryMap.put(langCode, countries);
        }
        return countries;

    }

}