package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "user_achievements")
public class UserAchievement {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "userid")
    private int userId;

    @Column(name = "achievementid")
    private int achievementId;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    public UserAchievement(){}

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
