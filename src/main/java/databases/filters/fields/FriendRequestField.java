package databases.filters.fields;

public enum FriendRequestField implements SqlField{
    ID("id"),
    FIRSTID("firstid"),
    SECONDID("secondid"),
    CREATIONDATE("creationdate");

    private String columnName;
    FriendRequestField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}