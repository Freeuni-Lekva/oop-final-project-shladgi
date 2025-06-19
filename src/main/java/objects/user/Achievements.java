package objects.user;

public class Achievements {
    private String id;
    private String iconLink;
    private AchievementRarity achivementRarity;

    public Achievements(String id, String iconLink, AchievementRarity achivementRarity) {
        this.id = id;
        this.iconLink = iconLink;
        this.achivementRarity = achivementRarity;
    }

    public String getId() {return id;}
    public String getIconLink() {return iconLink;}
    public AchievementRarity getAchivementRarity() {return achivementRarity;}
}
