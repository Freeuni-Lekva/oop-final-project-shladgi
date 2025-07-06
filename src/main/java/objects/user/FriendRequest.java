package objects.user;

//import com.google.errorprone.annotations.CanIgnoreReturnValue;
import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "friend_requests")
public class FriendRequest {

    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "firstid")
    private int firstId;

    @Column(name = "secondid")
    private int secondId;

    @Column(name = "creationdate")
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
