package objects.user;

import javax.sound.sampled.FloatControl;
import java.time.LocalDateTime;

public class Challenge {
    private int id, quizId, userId, friendId;
    private double bestScore;
    private String quizTitle;
    private LocalDateTime creationDate;

    public Challenge(int id, int quizId, int userId, int friendId, double bestScore, String quizTitle, LocalDateTime creationDate) {
        this.id = id;
        this.quizId = quizId;
        this.userId = userId;
        this.friendId = friendId;
        this.bestScore = bestScore;
        this.quizTitle = quizTitle;
        this.creationDate = creationDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getFriendId() { return friendId; }
    public void setFriendId(int friendId) { this.friendId = friendId; }
    public double getBestScore() { return bestScore; }
    public void setBestScore(double bestScore) { this.bestScore = bestScore; }
    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }
    public LocalDateTime getCreationDate() { return creationDate; }
}
