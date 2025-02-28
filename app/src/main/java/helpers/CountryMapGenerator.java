package helpers;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryMapGenerator {
    private static final Map<String, String> countryMap = generateCountryMap();

    public static Map<String, String> getCountryMap() {
        return countryMap;
    }
    private static Map<String, String> generateCountryMap() {
        Map<String, String> countryMap = new HashMap<>();
        countryMap.put("af", "افغانستان"); // Afghanistan - Pashto/Dari
        countryMap.put("al", "Shqipëria"); // Albania - Albanian
        countryMap.put("dz", "الجزائر"); // Algeria - Arabic
        countryMap.put("ad", "Andorra"); // Andorra - Catalan
        countryMap.put("ao", "Angola"); // Angola - Portuguese
        countryMap.put("ag", "Antigua and Barbuda"); // English
        countryMap.put("ar", "Argentina"); // Argentina - Spanish
        countryMap.put("am", "Հայաստան"); // Armenia - Armenian
        countryMap.put("au", "Australia"); // Australia - English
        countryMap.put("at", "Österreich"); // Austria - German
        countryMap.put("az", "Azərbaycan"); // Azerbaijan - Azerbaijani
        countryMap.put("bs", "Bahamas"); // Bahamas - English
        countryMap.put("bh", "البحرين"); // Bahrain - Arabic
        countryMap.put("bd", "বাংলাদেশ"); // Bangladesh - Bengali
        countryMap.put("bb", "Barbados"); // English
        countryMap.put("by", "Беларусь"); // Belarus - Belarusian
        countryMap.put("be", "België"); // Belgium - Dutch/French/German
        countryMap.put("bz", "Belize"); // Belize - English
        countryMap.put("bj", "Bénin"); // Benin - French
        countryMap.put("bo", "Bolivia"); // Bolivia - Spanish
        countryMap.put("ba", "Bosna i Hercegovina"); // Bosnia and Herzegovina - Bosnian
        countryMap.put("bw", "Botswana"); // Botswana - English
        countryMap.put("br", "Brasil"); // Brazil - Portuguese
        countryMap.put("bn", "Brunei"); // Brunei - Malay
        countryMap.put("bg", "България"); // Bulgaria - Bulgarian
        countryMap.put("bf", "Burkina Faso"); // Burkina Faso - French
        countryMap.put("bi", "Burundi"); // Burundi - Kirundi
        countryMap.put("cv", "Cabo Verde"); // Cape Verde - Portuguese
        countryMap.put("kh", "កម្ពុជា"); // Cambodia - Khmer
        countryMap.put("cm", "Cameroun"); // Cameroon - French/English
        countryMap.put("ca", "Canada"); // Canada - English/French
        countryMap.put("cf", "République centrafricaine"); // Central African Republic - French/Sango
        countryMap.put("td", "Tchad"); // Chad - French/Arabic
        countryMap.put("cl", "Chile"); // Chile - Spanish
        countryMap.put("cn", "中国"); // China - Chinese
        countryMap.put("co", "Colombia"); // Colombia - Spanish
        countryMap.put("km", "Comores"); // Comoros - Comorian/Arabic/French
        countryMap.put("cg", "Congo"); // Congo - French
        countryMap.put("cd", "Congo (DRC)"); // DR Congo - French
        countryMap.put("cr", "Costa Rica"); // Costa Rica - Spanish
        countryMap.put("ci", "Côte d'Ivoire"); // Ivory Coast - French
        countryMap.put("hr", "Hrvatska"); // Croatia - Croatian
        countryMap.put("cu", "Cuba"); // Cuba - Spanish
        countryMap.put("cy", "Κύπρος"); // Cyprus - Greek
        countryMap.put("dk", "Danmark"); // Denmark - Danish
        countryMap.put("dj", "جيبوتي"); // Djibouti - Arabic/French
        countryMap.put("dm", "Dominica"); // Dominica - English
        countryMap.put("do", "República Dominicana"); // Dominican Republic - Spanish
        countryMap.put("ec", "Ecuador"); // Ecuador - Spanish
        countryMap.put("eg", "مصر"); // Egypt - Arabic
        countryMap.put("sv", "El Salvador"); // El Salvador - Spanish
        countryMap.put("gq", "Guinea Ecuatorial"); // Equatorial Guinea - Spanish/French/Portuguese
        countryMap.put("er", "ኤርትራ"); // Eritrea - Tigrinya/Arabic/English
        countryMap.put("ee", "Eesti"); // Estonia - Estonian
        countryMap.put("et", "ኢትዮጵያ"); // Ethiopia - Amharic
        countryMap.put("fj", "Fiji"); // Fiji - English/Fijian/Hindi
        countryMap.put("fi", "Suomi"); // Finland - Finnish
        countryMap.put("fr", "France"); // France - French
        countryMap.put("ga", "Gabon"); // Gabon - French
        countryMap.put("gm", "Gambia"); // Gambia - English
        countryMap.put("ge", "საქართველო"); // Georgia - Georgian
        countryMap.put("gh", "Ghana"); // Ghana - English
        countryMap.put("gd", "Grenada"); // Grenada - English
        countryMap.put("gt", "Guatemala"); // Guatemala - Spanish
        countryMap.put("gn", "Guinée"); // Guinea - French
        countryMap.put("gw", "Guiné-Bissau"); // Guinea-Bissau - Portuguese
        countryMap.put("gy", "Guyana"); // Guyana - English

        countryMap.put("ht", "Haïti"); // Haiti - Haitian Creole/French
        countryMap.put("hn", "Honduras"); // Honduras - Spanish
        countryMap.put("hu", "Magyarország"); // Hungary - Hungarian
        countryMap.put("is", "Ísland"); // Iceland - Icelandic
        countryMap.put("in", "भारत"); // India - Hindi
        countryMap.put("id", "Indonesia"); // Indonesia - Indonesian
        countryMap.put("ir", "ایران"); // Iran - Persian
        countryMap.put("iq", "العراق"); // Iraq - Arabic/Kurdish
        countryMap.put("ie", "Éire"); // Ireland - Irish/English
        countryMap.put("il", "ישראל"); // Israel - Hebrew
        countryMap.put("it", "Italia"); // Italy - Italian
        countryMap.put("jm", "Jamaica"); // Jamaica - English
        countryMap.put("jp", "日本"); // Japan - Japanese
        countryMap.put("jo", "الأردن"); // Jordan - Arabic
        countryMap.put("kz", "Қазақстан"); // Kazakhstan - Kazakh/Russian
        countryMap.put("ke", "Kenya"); // Kenya - English/Swahili
        countryMap.put("ki", "Kiribati"); // Kiribati - English
        countryMap.put("kp", "조선"); // North Korea - Korean
        countryMap.put("kr", "대한민국"); // South Korea - Korean
        countryMap.put("kw", "الكويت"); // Kuwait - Arabic
        countryMap.put("kg", "Кыргызстан"); // Kyrgyzstan - Kyrgyz/Russian
        countryMap.put("la", "ປະເທດລາວ"); // Laos - Lao
        countryMap.put("lv", "Latvija"); // Latvia - Latvian
        countryMap.put("lb", "لبنان"); // Lebanon - Arabic
        countryMap.put("ls", "Lesotho"); // Lesotho - English/Sesotho
        countryMap.put("lr", "Liberia"); // Liberia - English
        countryMap.put("ly", "ليبيا"); // Libya - Arabic
        countryMap.put("li", "Liechtenstein"); // Liechtenstein - German
        countryMap.put("lt", "Lietuva"); // Lithuania - Lithuanian
        countryMap.put("lu", "Luxembourg"); // Luxembourg - Luxembourgish/French/German
        countryMap.put("mg", "Madagasikara"); // Madagascar - Malagasy/French
        countryMap.put("mw", "Malawi"); // Malawi - English/Chichewa
        countryMap.put("my", "Malaysia"); // Malaysia - Malay
        countryMap.put("mv", "Maldives"); // Maldives - Dhivehi
        countryMap.put("ml", "Mali"); // Mali - French
        countryMap.put("mt", "Malta"); // Malta - Maltese/English
        countryMap.put("mh", "Marshall Islands"); // Marshall Islands - English/Marshallese
        countryMap.put("mu", "Mauritius"); // Mauritius - English
        countryMap.put("mx", "México"); // Mexico - Spanish
        countryMap.put("fm", "Micronesia"); // Micronesia - English
        countryMap.put("md", "Moldova"); // Moldova - Romanian
        countryMap.put("mc", "Monaco"); // Monaco - French
        countryMap.put("mn", "Монгол"); // Mongolia - Mongolian
        countryMap.put("me", "Crna Gora"); // Montenegro - Montenegrin
        countryMap.put("ma", "المغرب"); // Morocco - Arabic
        countryMap.put("mz", "Moçambique"); // Mozambique - Portuguese
        countryMap.put("mm", "မြန်မာ"); // Myanmar - Burmese
        countryMap.put("na", "Namibia"); // Namibia - English
        countryMap.put("nr", "Nauru"); // Nauru - Nauruan/English
        countryMap.put("np", "नेपाल"); // Nepal - Nepali
        countryMap.put("nl", "Nederland"); // Netherlands - Dutch
        countryMap.put("nz", "New Zealand"); // New Zealand - English/Māori
        countryMap.put("ni", "Nicaragua"); // Nicaragua - Spanish
        countryMap.put("ne", "Niger"); // Niger - French
        countryMap.put("ng", "Nigeria"); // Nigeria - English
        countryMap.put("no", "Norge"); // Norway - Norwegian
        countryMap.put("om", "عمان"); // Oman - Arabic
        countryMap.put("pk", "پاکستان"); // Pakistan - Urdu
        countryMap.put("pw", "Palau"); // Palau - English
        countryMap.put("ps", "فلسطين"); // Palestine - Arabic
        countryMap.put("pa", "Panamá"); // Panama - Spanish
        countryMap.put("pg", "Papua New Guinea"); // Papua New Guinea - English/Hiri Motu
        countryMap.put("py", "Paraguay"); // Paraguay - Spanish/Guarani
        countryMap.put("pe", "Perú"); // Peru - Spanish
        countryMap.put("ph", "Pilipinas"); // Philippines - Filipino/English
        countryMap.put("pl", "Polska"); // Poland - Polish
        countryMap.put("pt", "Portugal"); // Portugal - Portuguese
        countryMap.put("qa", "قطر"); // Qatar - Arabic
        countryMap.put("ro", "România"); // Romania - Romanian
        countryMap.put("ru", "Россия"); // Russia - Russian
        countryMap.put("rw", "Rwanda"); // Rwanda - Kinyarwanda
        countryMap.put("kn", "Saint Kitts and Nevis"); // English
        countryMap.put("lc", "Saint Lucia"); // English
        countryMap.put("vc", "Saint Vincent and the Grenadines"); // English
        countryMap.put("ws", "Samoa"); // Samoan/English
        countryMap.put("sm", "San Marino"); // Italian
        countryMap.put("st", "São Tomé e Príncipe"); // Portuguese
        countryMap.put("sa", "السعودية"); // Saudi Arabia - Arabic
        countryMap.put("sn", "Sénégal"); // Senegal - French
        countryMap.put("rs", "Србија"); // Serbia - Serbian
        countryMap.put("sc", "Seychelles"); // English/French
        countryMap.put("sl", "Sierra Leone"); // English
        countryMap.put("sg", "Singapore"); // English/Malay/Tamil/Mandarin
        countryMap.put("sk", "Slovensko"); // Slovakia - Slovak
        countryMap.put("si", "Slovenija"); // Slovenia - Slovenian
        countryMap.put("sb", "Solomon Islands"); // English
        countryMap.put("so", "Soomaaliya"); // Somalia - Somali
        countryMap.put("za", "South Africa"); // English/Afrikaans/Zulu/Xhosa, etc.
        countryMap.put("kr", "대한민국"); // South Korea - Korean
        countryMap.put("ss", "South Sudan"); // English
        countryMap.put("es", "España"); // Spain - Spanish
        countryMap.put("lk", "ශ්‍රී ලංකාව"); // Sri Lanka - Sinhala/Tamil
        countryMap.put("sd", "السودان"); // Sudan - Arabic
        countryMap.put("sr", "Suriname"); // Dutch
        countryMap.put("se", "Sverige"); // Sweden - Swedish
        countryMap.put("ch", "Schweiz"); // Switzerland - German/French/Italian
        countryMap.put("sy", "سوريا"); // Syria - Arabic
        countryMap.put("tw", "台灣"); // Taiwan - Chinese
        countryMap.put("tj", "Тоҷикистон"); // Tajikistan - Tajik
        countryMap.put("tz", "Tanzania"); // Tanzania - Swahili
        countryMap.put("th", "ประเทศไทย"); // Thailand - Thai
        countryMap.put("tl", "Timor-Leste"); // Portuguese/Tetum
        countryMap.put("tg", "Togo"); // French
        countryMap.put("to", "Tonga"); // Tongan/English
        countryMap.put("tt", "Trinidad and Tobago"); // English
        countryMap.put("tn", "تونس"); // Tunisia - Arabic
        countryMap.put("tr", "Türkiye"); // Turkey - Turkish
        countryMap.put("tm", "Türkmenistan"); // Turkmenistan - Turkmen
        countryMap.put("tv", "Tuvalu"); // English/Tuvaluan
        countryMap.put("ug", "Uganda"); // Uganda - English/Swahili
        countryMap.put("ua", "Україна"); // Ukraine - Ukrainian
        countryMap.put("ae", "الإمارات العربية المتحدة"); // UAE - Arabic
        countryMap.put("gb", "United Kingdom"); // English
        countryMap.put("us", "United States"); // English
        countryMap.put("uy", "Uruguay"); // Spanish
        countryMap.put("uz", "Oʻzbekiston"); // Uzbekistan - Uzbek
        countryMap.put("vu", "Vanuatu"); // Bislama/French/English
        countryMap.put("va", "Vatican City"); // Latin/Italian
        countryMap.put("ve", "Venezuela"); // Spanish
        countryMap.put("vn", "Việt Nam"); // Vietnam - Vietnamese
        countryMap.put("ye", "اليمن"); // Yemen - Arabic
        countryMap.put("zm", "Zambia"); // English
        countryMap.put("zw", "Zimbabwe"); // English
        return countryMap;
    }

}