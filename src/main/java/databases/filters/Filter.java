package databases.filters;

public interface Filter<T>{
    boolean filter(T x);
}
