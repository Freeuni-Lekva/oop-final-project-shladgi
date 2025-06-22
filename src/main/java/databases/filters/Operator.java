package databases.filters;

public enum Operator {
    EQUALS("="),
    MOREEQ(">="),
    LESSEQ("<="),
    MORE(">"),
    LESS("<"),
    LIKE("LIKE");

    private final String operator;
    Operator(String op){
        this.operator = op;
    }

    public String getOperator() {return operator;}
}
