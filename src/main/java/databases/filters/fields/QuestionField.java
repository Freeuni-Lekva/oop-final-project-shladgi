package databases.filters.fields;

import objects.questions.QType;

public enum QuestionField implements SqlField{
    ID("id"),
    QUIZID("quizid"),
    WEIGHT("weight"),
    TYPE("type");

    private final String columnName;
    QuestionField(String columnName){
        this.columnName = columnName;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}
