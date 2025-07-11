package databases.filters.fields;

import objects.user.UserToken;

public enum UserTokenField implements SqlField{
    ID("id"),
    TOKEN("token"),
    USERID("userid"),
    EXPIREDATE("expiredate");

    private String columnName;
    UserTokenField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
