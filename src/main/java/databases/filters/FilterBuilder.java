package databases.filters;


import java.util.List;

// this class is to buils a sql WHERE clause from the List of FilterConditions
public class FilterBuilder {

    /**
     * Builds the String for sql where clause from the List of FilterConditions
     * @param filters FiltedConditions from which the String will be built
     * @return String that will have given filters for example "(true) AND (id = 5) AND (name = 'john')"
     */
    public static String buildFilter(List<? extends FilterCondition> filters) {
        if (filters == null || filters.isEmpty()) return "(true)"; // base case

        StringBuilder builder = new StringBuilder("(true)");

        for (FilterCondition<?> condition : filters) {
            builder.append(" AND ");
            builder.append("(" + condition.getColumnName() + " " + condition.getOperator() + " " + condition.getValue() + ")");
        }

        return builder.toString();
    }
}
