package objects.user;

import java.time.LocalDateTime;

public class UserAchievement {
    private int id, userId, achievementId;
    private LocalDateTime creationDate;

    public UserAchievement(int userId, int achievementId, LocalDateTime creationDate) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.creationDate = creationDate;
    }

    public UserAchievement(int id, int userId, int achievementId, LocalDateTime creationDate) {
        this.id = id;
        this.userId = userId;
        this.achievementId = achievementId;
        this.creationDate = creationDate;
    }

    public int  getId() {return id;}
    public int getUserId() {return userId;}
    public int getAchievementId() {return achievementId;}
    public LocalDateTime getCreationDate() {return creationDate;}
}
