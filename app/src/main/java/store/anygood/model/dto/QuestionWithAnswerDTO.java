package store.anygood.model.dto;


import java.util.List;

public class QuestionWithAnswerDTO {
    private QuestionDTO question;
    private List<String> answers;

    // Constructors, getters, setters


    public QuestionWithAnswerDTO() {
    }

    public QuestionWithAnswerDTO(QuestionDTO question) {
        this.question = question;
    }

    public QuestionWithAnswerDTO(QuestionDTO question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    public QuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(QuestionDTO questionDTO) {
        this.question = questionDTO;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
}
