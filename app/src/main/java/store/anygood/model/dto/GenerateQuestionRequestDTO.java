package store.anygood.model.dto;


public class GenerateQuestionRequestDTO {
    private String initialQuery;
    private String selectedCountry;
    private String selectedLanguage;
    private QuestionWithAnswerDTO questionWithAnswers;

    // Getters and setters

    public String getInitialQuery() {
        return initialQuery;
    }

    public void setInitialQuery(String initialQuery) {
        this.initialQuery = initialQuery;
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public String getSelectedLanguage() {
        return selectedLanguage;
    }

    public void setSelectedLanguage(String selectedLanguage) {
        this.selectedLanguage = selectedLanguage;
    }

    public QuestionWithAnswerDTO getQuestionWithAnswers() {
        return questionWithAnswers;
    }

    public void setQuestionWithAnswers(QuestionWithAnswerDTO questionWithAnswers) {
        this.questionWithAnswers = questionWithAnswers;
    }
}
