package databases;

import databases.filters.Filter;
import databases.filters.FilterBuilder;
import databases.filters.FilterCondition;
import databases.filters.fields.Field;
import objects.questions.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class DataBase<T, TField extends Field>{
    Connection con;


    DataBase(Connection con){
        this.con = con;
    }

    /**
     * Add the object to Data Base
     * @param entity the object to add
     */
    public void add(T entity){
        try{
            PreparedStatement stmt = prepareAddStatement(entity);
            int rows = stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) setIdWithResultSet(entity, rs);
            rs.close();
        }catch (Exception e){
            throw new RuntimeException("ADD ERROR " + e.getMessage());
        }
    }

    /**
     * Delete all the questions satisfying the Filter
     * @param filterConditions filter conditions
     * @return number of rows deleted
     */
    public int delete(List<FilterCondition<TField>> filterConditions){
        String filterString = FilterBuilder.buildFilter(filterConditions);
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM questions WHERE " + filterString)) {
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DELETE ERROR " + e.getMessage());
        }
    }

    /**
     * Query the database with the filter
     * @param filterConditions filter conditions
     * @return List of objects that are included in the filter converted to objects
     */
    public List<T> query(List<FilterCondition<TField>> filterConditions){
        List<T> list = new ArrayList<>();
        String filterString = FilterBuilder.buildFilter(filterConditions);
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM questions WHERE "+filterString)){
            ResultSet rs = stmt.executeQuery();

            // loop over the query result rows and add them to the list
            while(rs.next()) list.add(convert(rs));

            rs.close();
        }catch (Exception e){
            throw new RuntimeException("QUERY ERROR " + e.getMessage());
        }
        return list;
    }

    public List<T> query(FilterCondition<TField>... filter){
        return query(List.of(filter));
    }

    public int delete(FilterCondition<TField>... filter){
        return delete(List.of(filter));
    }

    protected abstract T convert(ResultSet rs);
    protected abstract void setIdWithResultSet(T entity, ResultSet rs);
    protected abstract PreparedStatement prepareAddStatement(T entity);

}
