package databases.filters.fields;

public enum ChallengeField implements SqlField{
    ID("id"),
    QUIZID("quizid"),
    SENDERID("senderid"),
    RECIPIENTID("recipientid"),
    CREATIONDATE("creationdate"),
    VIEWED("viewed");

    private final String columnName;
    ChallengeField(String columnName){
        this.columnName = columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}