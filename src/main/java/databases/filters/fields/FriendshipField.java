package databases.filters.fields;

public enum FriendshipField implements SqlField{
    ID("id"),
    FIRSTID("firstid"),
    SECONDID("secondid"),
    CREATIONDATE("creationdate");

    private String ColumnName;
    FriendshipField(String columnName){
        this.ColumnName = columnName;
    }
    @Override
    public String getColumnName() {
        return ColumnName;
    }
}
