package objects;

import java.time.LocalDateTime;

public class Quiz {
    private int id;
    private String title;
    private int userId;
    private LocalDateTime creationDate;
    private double totalScore;
    private boolean isRandom, singlePage, immediateCorrection;

    public Quiz(){}

    public Quiz(String title, int userId, LocalDateTime creationDate, double totalScore, boolean isRandom, boolean isSinglePage, boolean isImmediateCorrection) {
        this.title = title;
        this.userId = userId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
        this.isRandom = isRandom;
        this.singlePage = isSinglePage;
        this.immediateCorrection = isImmediateCorrection;
    }

    public Quiz(int id, String title, int userId, LocalDateTime creationDate, double totalScore,  boolean isRandom, boolean isSinglePage) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
        this.isRandom = isRandom;
        this.singlePage = isSinglePage;
        this.immediateCorrection = false;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
    public LocalDateTime getCreationDate() {return creationDate;}
    public void setCreationDate(LocalDateTime creationDate) {this.creationDate = creationDate;}
    public double getTotalScore() {return totalScore;}
    public void setTotalScore(double totalScore) {this.totalScore = totalScore;}
    public boolean isRandom() {return isRandom;}
    public void setRandom(boolean random) {isRandom = random;}
    public boolean isSinglePage() {return singlePage;}
    public void setSinglePage(boolean singlePage) {this.singlePage = singlePage;}
    public boolean isImmediateCorrection() {return immediateCorrection;}
    public void setImmediateCorrection(boolean immediateCorrection) {this.immediateCorrection = immediateCorrection;}
}
