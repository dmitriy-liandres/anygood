package store.anygood.model.dto;


import java.util.List;

public class QuestionDTO {

    private Long questionId;
    private String text;
    private List<String> options;
    private boolean allowFreeText;
    private boolean last;

    public QuestionDTO() {

    }

    public QuestionDTO(String text) {
        this.text = text;
    }


    public QuestionDTO(String text, List<String> options, boolean allowFreeText, boolean last) {
        this.text = text;
        this.options = options;
        this.allowFreeText = allowFreeText;
        this.last = last;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
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
        return last;
    }

    public Long getQuestionId() {
        return questionId;
    }
}
