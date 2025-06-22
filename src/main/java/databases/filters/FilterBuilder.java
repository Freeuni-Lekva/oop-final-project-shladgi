package databases.filters;

import databases.filters.fields.Field;

import java.util.List;

public class FilterBuilder {
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
