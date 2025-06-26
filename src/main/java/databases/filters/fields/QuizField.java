package databases.filters.fields;

public enum QuizField implements SqlField{
    ID("id"),
    TITLE("title"),
    USERID("userid"),
    CREATIONDATE("creationdate"),
    TOTALSCORE("totalscore"),
    TOTALQUESTIONS("totalquestions"),
    RANDOM("random"),
    SINGLEPAGE("singlepage"),
    IMMEDCORRECTION("immediatecorrection");

    private String columnName;
    QuizField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}