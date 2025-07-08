package databases.filters.fields;

public enum NoteField implements SqlField{
    ID("id"),
    SENDERID("senderid"),
    RECIPIENTID("recipientid"),
    CREATIONDATE("creationdate"),
    TEXT("text"),
    VIEWED("viewed");

    private final String columnName;
    NoteField(String columnName){
        this.columnName = columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}