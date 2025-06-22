package objects.user;

public class Achievement {
    private int id;
    private String title;
    private String description;
    private String iconLink;
    private AchievementRarity achivementRarity;

    public Achievement(int id, String iconLink, AchievementRarity achivementRarity) {
        this.id = id;
        this.iconLink = iconLink;
        this.achivementRarity = achivementRarity;
    }

    public int getId() {return id;}
    public String getIconLink() {return iconLink;}
    public AchievementRarity getAchivementRarity() {return achivementRarity;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
}
