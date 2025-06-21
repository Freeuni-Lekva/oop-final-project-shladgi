package databases.filters;

public interface Filter<T>{
    /**
     * Checks if a given object satisfies the filter
     * @param x is the object you want to check
     * @return True of False
     */
    boolean filter(T x);
}
