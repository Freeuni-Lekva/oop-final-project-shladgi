package databases.filters.fields;

public enum QuizResultField implements SqlField{
    ID("id"),
    QUIZID("quizid"),
    USERID("userid"),
    SCORE("totalscore"),
    CREATIONDATE("creationdate");

    private String columnName;
    QuizResultField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
