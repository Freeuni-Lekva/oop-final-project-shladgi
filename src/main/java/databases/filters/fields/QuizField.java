package databases.filters.fields;

public enum QuizField implements SqlField{
    ID("id"),
    TITLE("title"),
    USERID("userid"),
    CREATIONDATE("creationdate"),
    TIMELIMIT("timelimit"),
    TOTALSCORE("totalscore"),
    TOTALQUESTIONS("totalquestions"),
    RANDOM("random"),
    SINGLEPAGE("singlepage"),
    IMMEDCORRECTION("immediatecorrection"),
    DESCRIPTION("description"),
    PRACTICEMODE("practicemode");


    private String columnName;
    QuizField(String columnName){
        this.columnName = columnName;
    }
    @Override
    public String getColumnName() {
        return columnName;
    }
}