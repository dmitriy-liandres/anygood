package store.anygood;

import java.util.List;

public class Question {
    private String text;
    private List<String> options;
    private boolean allowFreeText;
    private boolean isLast;

    public Question(String text, List<String> options, boolean allowFreeText, boolean isLast) {
        this.text = text;
        this.options = options;
        this.allowFreeText = allowFreeText;
        this.isLast = isLast;
    }

    public String getText() {
        return text;
    }

    public List<String> getOptions() {
        return options;
    }

    public boolean isAllowFreeText() {
        return allowFreeText;
    }

    public boolean isLast() {
        return isLast;
    }
}
