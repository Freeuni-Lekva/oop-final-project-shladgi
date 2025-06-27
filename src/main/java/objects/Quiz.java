package objects;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "quizzes")
public class Quiz {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "userid")
    private int userId;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    @Column(name = "timelimit")
    private int timeLimit; // seconds

    @Column(name = "totalscore")
    private double totalScore;

    @Column(name = "totalquestions")
    private int totalQuestions;

    @Column(name = "random")
    private boolean isRandom;

    @Column(name = "singlepage")
    private boolean singlePage;

    @Column(name = "immediatecorrection")
    private boolean immediateCorrection;

    @Column(name = "practicemode")
    private boolean practiceMode;

    public Quiz(){}

    public Quiz(String title, int userId, LocalDateTime creationDate, int timeLimit, double totalScore, int totalQuestions, boolean isRandom, boolean isSinglePage, boolean isImmediateCorrection, boolean practiceMode) {
        this.title = title;
        this.userId = userId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
        this.isRandom = isRandom;
        this.singlePage = isSinglePage;
        this.totalQuestions =totalQuestions;
        this.immediateCorrection = isImmediateCorrection;
        this.practiceMode = practiceMode;
        this.timeLimit = timeLimit;
    }

    public Quiz(int id, String title, int userId, LocalDateTime creationDate, int timeLimit, double totalScore, int totalQuestions, boolean isRandom, boolean isSinglePage, boolean isImmediateCorrection, boolean practiceMode) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
        this.isRandom = isRandom;
        this.totalQuestions = totalQuestions;
        this.singlePage = isSinglePage;
        this.immediateCorrection = isImmediateCorrection;
        this.practiceMode = practiceMode;
        this.timeLimit = timeLimit;
    }

    public int getTotalQuestions() {return totalQuestions;}
    public void setTotalQuestions(int totalQuestions) {this.totalQuestions = totalQuestions;}
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
    public void setPracticeMode(boolean practiceMode) {this.practiceMode = practiceMode;}
    public boolean isPracticeMode() {return this.practiceMode;}
}
