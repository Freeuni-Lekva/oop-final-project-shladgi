package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "notes")
public class Note {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "senderid")
    private int senderId;

    @Column(name = "recipientid")
    private int recipientId;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    @Column(name = "text")
    private String text;

    @Column(name = "viewed")
    private boolean viewed = false;



    public Note(){}

    public Note(int senderId, int recipientId, LocalDateTime creationDate, String text, boolean isViewed) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.creationDate = creationDate;
        this.text = text;
        this.viewed = isViewed;
    }
    public Note (int id, int senderId, int recipientId,LocalDateTime creationDate, String text, boolean isViewed) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.creationDate = creationDate;
        this.text = text;
        this.viewed = isViewed;
    }
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public int getSenderId() {return senderId;}
    public void setSenderId(int senderId) {this.senderId = senderId;}
    public int getFriendId() {return recipientId;}
    public void setFriendId(int recipientId) {this.recipientId = recipientId;}
    public LocalDateTime getCreationDate() {return creationDate;}
    public void setViewed(boolean viewed) {this.viewed = viewed;}
    public boolean isViewed() {return viewed;}
    public String getText() {return text;}
}
