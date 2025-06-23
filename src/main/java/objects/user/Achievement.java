package objects.user;


import databases.annotations.Column;
import databases.annotations.Table;

@Table(name = "achievements")
public class Achievement {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "iconlink")
    private String iconLink;

    @Column(name = "rarity")
    private AchievementRarity achivementRarity;

    public Achievement(){}

    public Achievement(String title, String description, String iconLink, AchievementRarity achivementRarity) {
        this.iconLink = iconLink;
        this.achivementRarity = achivementRarity;
        this.title = title;
        this.description = description;
    }
    public Achievement(int id,String title, String description, String iconLink, AchievementRarity achivementRarity) {
        this.id = id;
        this.iconLink = iconLink;
        this.achivementRarity = achivementRarity;
        this.title = title;
        this.description = description;
    }

    public int getId() {return id;}
    public String getIconLink() {return iconLink;}
    public AchievementRarity getAchivementRarity() {return achivementRarity;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
}
