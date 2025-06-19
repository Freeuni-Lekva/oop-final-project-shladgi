package databases;

import databases.filters.Filter;
import objects.questions.Question;

import java.sql.Connection;
import java.util.List;

public abstract class DataBase<T>{

    /**
     * Add the object to Data Base
     * @param entity the object to add
     */
    public abstract void add(T entity);

    /**
     * Delete all the questions satisfying the Filter
     * @param filter
     * @return amount of rows deleted
     */
    public abstract int delete(Filter<T> filter);

    /**
     * Query the database with the filter
     * @param filter
     * @return List of objects that are included in the filter converted to objects
     */
    public abstract List<T> query(Filter<T> filter);

}
