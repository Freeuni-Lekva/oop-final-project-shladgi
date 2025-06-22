package objects.user;

import java.time.LocalDateTime;

public class Friendship {
    private int id,firstId, secondId;
    private LocalDateTime creationDate;

    public Friendship(int firstId, int secondId,  LocalDateTime creationDate) {
        this.firstId = firstId;
        this.secondId = secondId;
        this.creationDate = creationDate;
    }
    public Friendship(int id,int firstId, int secondId,  LocalDateTime creationDate) {
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
