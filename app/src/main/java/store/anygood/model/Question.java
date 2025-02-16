package store.anygood.model;


import java.util.List;

public class Question {

    private String text;
    private List<String> options;
    private boolean allowFreeText;
    private boolean last;

    public Question() {

    }

    public Question(String text) {
        this.text = text;
    }


    public Question(String text, List<String> options, boolean allowFreeText, boolean last) {
        this.text = text;
        this.options = options;
        this.allowFreeText = allowFreeText;
        this.last = last;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public boolean isAllowFreeText() {
        return allowFreeText;
    }

    public void setAllowFreeText(boolean allowFreeText) {
        this.allowFreeText = allowFreeText;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
