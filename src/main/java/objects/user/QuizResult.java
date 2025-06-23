package objects.user;

import java.time.LocalDateTime;

public class QuizResult {
    private int id, userId, quizId;
    private LocalDateTime creationDate;
    private double totalScore;

    public QuizResult(){}

    public QuizResult(int userId, int quizId, LocalDateTime creationDate, double totalScore) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
    }

    public QuizResult(int id, int userId, int quizId, LocalDateTime creationDate, double totalScore) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
    }

    public int getId() {return id;}
    public int getUserId() {return userId;}
    public int getQuizId() {return quizId;}
    public LocalDateTime getCreationDate() {return creationDate;}
    public double getTotalScore() {return totalScore;}
}
