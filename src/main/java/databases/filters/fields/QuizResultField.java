package databases.filters.fields;

public enum QuizResultField implements SqlField{
    ID("id"),
    QUIZID("quizid"),
    USERID("userid"),
    TOTALSCORE("totalscore"),
    CREATIONDATE("creationdate"),
    TIMETAKEN("timetaken");

    private String columnName;
    QuizResultField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}
