package databases.filters.fields;

public enum UserField implements SqlField{
    ID("id"),
    USERNAME("username"),
    PASSWORD("password"),
    SALT("salt"),
    TYPE("type"),
    CREATION_DATE("creationdate");

    private final String columnName;
    UserField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
