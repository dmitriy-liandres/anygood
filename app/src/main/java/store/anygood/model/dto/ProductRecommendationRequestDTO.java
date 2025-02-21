package store.anygood.model.dto;


import java.util.List;

public class ProductRecommendationRequestDTO {
    private String initialQuery;
    private String selectedCountry;
    private String selectedLanguage;
    private List<QuestionWithAnswerDTO> questionsWithAnswers;
    private String additionalInfo;

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

    public List<QuestionWithAnswerDTO> getQuestionsWithAnswers() {
        return questionsWithAnswers;
    }

    public void setQuestionsWithAnswers(List<QuestionWithAnswerDTO> questionsWithAnswers) {
        this.questionsWithAnswers = questionsWithAnswers;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
