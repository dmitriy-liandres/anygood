package store.anygood;

import android.net.Uri;

public class AmazonApiClient {
    /**
     * Generate an Amazon search link using the given keyword.
     * The format is:
     * https://www.amazon.com/s?k={keyword}&tag=anygood0d-20&language=en_US
     */
    public static String generateAmazonLink(String keyword) {
        String encodedKeyword = Uri.encode(keyword);
        return "https://www.amazon.com/s?k=" + encodedKeyword + "&tag=anygood0d-20&language=en_US";
    }
}
