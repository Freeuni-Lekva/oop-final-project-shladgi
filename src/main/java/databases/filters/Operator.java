package databases.filters;

// this is an enum for database operations
public enum Operator {
    EQUALS("="),
    MOREEQ(">="),
    LESSEQ("<="),
    MORE(">"),
    LESS("<"),
    NOTEQ("!="),
    LIKE("LIKE");

    private final String operator;
    Operator(String op){
        this.operator = op;
    }

    public String getOperator() {return operator;}
}
