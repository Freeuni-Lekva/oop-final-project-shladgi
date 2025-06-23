package databases.filters;

import databases.filters.fields.SqlField;

public class FilterCondition<T extends SqlField>{
    private T field;
    private Operator operator;
    private String value;
    public FilterCondition(T field, Operator operator, String value){
        this.field = field;
        this.operator = operator;
        this.value = "'" + value + "'"; // if the value is "abc" it will be converted to "'abc'" for database
    }
    public FilterCondition(T field, Operator operator, int value){
        this.field = field;
        this.operator = operator;
        this.value = Integer.toString(value); // if the value is 5 it will be converted to "5" for database
    }

    public FilterCondition(T field, Operator operator, double value){
        this.field = field;
        this.operator = operator;
        this.value = Double.toString(value); // if the value is 5.0 it will be converted to "5.0" for database
    }

    public FilterCondition(T field, Operator operator, boolean value){
        this.field = field;
        this.operator = operator;
        this.value = Boolean.toString(value); // if the value is true it will be converted to "true" for database
    }
    public String getColumnName() {return field.getColumnName();}
    public String getOperator() {return operator.getOperator();}
    public String getValue() {return value;}
}
