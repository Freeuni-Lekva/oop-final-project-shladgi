package objects;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "announcements")
public class Announcement {

    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "imagelink")
    private String imageLink;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "author")
    private String author;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    public Announcement() {}

    public Announcement(String title, String content, String author, String imageLink, LocalDateTime creationDate) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.imageLink = imageLink;
        this.creationDate = creationDate;
    }


    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getImageLink() {return imageLink;}
    public void setImageLink(String imageLink) {this.imageLink = imageLink;}
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
    public String getAuthor() {return author;}
    public void setAuthor(String author) {this.author = author;}
}
