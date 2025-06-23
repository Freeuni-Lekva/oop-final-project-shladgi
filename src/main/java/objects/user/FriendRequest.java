package objects.user;

import java.time.LocalDateTime;

public class FriendRequest {
    private int id, firstId, secondId;
    private LocalDateTime creationDate;

    public FriendRequest(){}

    public FriendRequest(int firstId, int secondId, LocalDateTime creationDate) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.creationDate = creationDate;
    }

    public FriendRequest(int id, int firstId, int secondId, LocalDateTime creationDate) {
        this.id = id;
        this.firstId = firstId;
        this.secondId = secondId;
        this.creationDate = creationDate;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getFirstId() {return firstId;}
    public void setFirstId(int firstId) {this.firstId = firstId;}
    public int getSecondId() {return secondId;}
    public void setSecondId(int secondId) {this.secondId = secondId;}
    public LocalDateTime getCreationDate() {return creationDate;}
}
