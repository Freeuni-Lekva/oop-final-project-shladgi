package databases.filters.fields;

public enum UserAnswerField implements SqlField{
    ID("id"),
    QUESTIONID("questionid"),
    RESULTID("resultid");

    private final String columnName;
    UserAnswerField(String columnName){
        this.columnName = columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}
