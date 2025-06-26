package objects.user;

import java.time.LocalDateTime;

public class Note {
    private int id, userId, friendId;
    private LocalDateTime creationDate;
    private String text;

    public Note(int userId, int friendId, LocalDateTime creationDate, String text) {
        this.userId = userId;
        this.friendId = friendId;
        this.creationDate = creationDate;
        this.text = text;
    }
    public Note (int id, int userId, int friendId,LocalDateTime creationDate, String text) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.creationDate = creationDate;
        this.text = text;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getUserId() {return userId;}
    public void setUserId(int userId) {this.userId = userId;}
    public int getFriendId() {return friendId;}
    public void setFriendId(int friendId) {this.friendId = friendId;}
    public LocalDateTime getCreationDate() {return creationDate;}
}
