package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "friendships")
public class Friendship {

    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "firstid")
    private int firstId;

    @Column(name = "secondid")
    private int secondId;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    public Friendship(){}

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
