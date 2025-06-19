package objects.user;

import java.time.LocalDateTime;

public class FriendShip {
    private int id,firstId, secondId;
    private LocalDateTime creationDate;

    public FriendShip(int firstId, int secondId,  LocalDateTime creationDate) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.creationDate = creationDate;
    }
    public FriendShip(int id,int firstId, int secondId,  LocalDateTime creationDate) {
        this.id = id;
        this.firstId = firstId;
        this.secondId = secondId;
        this.creationDate = creationDate;
    }

    public int getId () {return id;}
    public int getFirstId(){return firstId;}
    public int getSecondId(){return secondId;}
    public LocalDateTime getCreationDate() {return creationDate;}
}
