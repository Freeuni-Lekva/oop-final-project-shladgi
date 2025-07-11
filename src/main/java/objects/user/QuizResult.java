package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "quiz_results")
public class QuizResult {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "userid")
    private int userId;

    @Column(name = "quizid")
    private int quizId;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    // time taken -1 means that it is not finished yet and is in progress
    @Column(name = "timetaken")
    private int timeTaken; // seconds

    @Column(name = "totalscore")
    private double totalScore;


    public QuizResult(){}

    public QuizResult(int userId, int quizId, LocalDateTime creationDate, double totalScore, int timeTaken) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
        this.timeTaken = timeTaken;
    }

    public QuizResult(int id, int userId, int quizId, LocalDateTime creationDate, double totalScore, int timeTaken) {
        this.id = id;
        this.userId = userId;
        this.quizId = quizId;
        this.creationDate = creationDate;
        this.totalScore = totalScore;
        this.timeTaken = timeTaken;

    }

    public int getId() {return id;}
    public int getUserId() {return userId;}
    public int getQuizId() {return quizId;}
    public LocalDateTime getCreationDate() {return creationDate;}
    public double getTotalScore() {return totalScore;}
    public int getTimeTaken() {return this.timeTaken;}
    public void setTimeTaken(int seconds) {this.timeTaken = seconds;}
    public void setTotalScore(double points){this.totalScore = points;}
    public void setQuizId(int quizId){this.quizId = quizId;}
    public void setUserId(int userId){this.userId = userId;};
    public void setCreationDate(LocalDateTime time){this.creationDate = time;}

}
