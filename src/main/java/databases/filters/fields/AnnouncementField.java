package databases.filters.fields;

public enum AnnouncementField implements SqlField{
    ID("id"),
    TITLE("title"),
    AUTHOR("author"),
    CREATIONDATE("creationdate");


    private String columnName;
    AnnouncementField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
