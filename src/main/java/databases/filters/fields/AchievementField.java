package databases.filters.fields;

public enum AchievementField implements SqlField{
    ID("id"),
    TITLE("title"),
    DESCRIPTION("description"),
    RARITY("rarity");

    private String columnName;
    AchievementField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
