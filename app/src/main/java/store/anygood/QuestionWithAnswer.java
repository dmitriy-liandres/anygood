package store.anygood;

public class QuestionWithAnswer {
    private Question question;
    private String answer;

    public QuestionWithAnswer(Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
