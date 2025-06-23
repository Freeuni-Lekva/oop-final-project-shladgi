package databases.filters.fields;

public enum AchievementField implements SqlField{
    ID("id"),
    TITLE("title"),
    RARITY("rarity"),
    CREATIONDATE("creationdate");

    private String columnName;
    AchievementField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
