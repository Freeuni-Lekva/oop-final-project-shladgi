package databases.filters.fields;

public enum UserAchievementField implements SqlField{
    ID("id"),
    USERID("userid"),
    ACHIEVEMENTID("achievementid"),
    CREATIONDATE("creationdate");

    private String columnName;
    UserAchievementField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
