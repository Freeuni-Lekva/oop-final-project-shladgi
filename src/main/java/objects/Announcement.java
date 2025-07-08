package objects;

import databases.annotations.Column;
import databases.annotations.Table;

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





}
