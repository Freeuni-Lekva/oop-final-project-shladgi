package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "challenges")
public class Challenge {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "quizid")
    private int quizId;

    @Column(name = "senderid")
    private int senderId;

    @Column(name = "recipientid")
    private int recipientId;

    @Column(name = "bestscore")
    private double bestScore;

    @Column(name = "quiztitle")
    private String quizTitle;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    public Challenge(){}

    public Challenge(int id, int quizId, int senderId, int recipientId, double bestScore, String quizTitle, LocalDateTime creationDate) {
        this.id = id;
        this.quizId = quizId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.bestScore = bestScore;
        this.quizTitle = quizTitle;
        this.creationDate = creationDate;
    }
    public Challenge(int quizId, int senderId, int recipientId, double bestScore, String quizTitle, LocalDateTime creationDate) {
        this.quizId = quizId;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.bestScore = bestScore;
        this.quizTitle = quizTitle;
        this.creationDate = creationDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getRecipiantId() { return recipientId; }
    public void setRecipiantId(int recipientId) { this.recipientId = recipientId; }
    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
    public LocalDateTime getCreationDate() { return creationDate; }
}
