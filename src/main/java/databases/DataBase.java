package databases;

import databases.annotations.AnnotationUtil;
import databases.annotations.Table;
import databases.filters.FilterBuilder;
import databases.filters.FilterCondition;
import databases.filters.fields.SqlField;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

public abstract class DataBase<T, TField extends SqlField>{
    protected Connection con;
    protected Class<T> clazz;
    protected String tableName = null;

    /**
     * Constructor that gets the Connection to the database and the Class for the Field it accepts(for filters)
     * @param con Connection
     * @param clazz SqlField Subclass that corresponds to the Class this database is for
     */
    public DataBase(Connection con, Class<T> clazz){
        this.con = con;
        this.clazz = clazz;
        initTable();
    }

    /**
     * Initalizes the tableName variable for use
     */
    private void initTable(){
        List<Annotation> classAnnotations = AnnotationUtil.getClassAnnotationsWithAncestors(clazz);
        // look fot the @Table annotation
        for(Annotation a : classAnnotations){
            if(a.annotationType().equals(Table.class)){
                if(tableName != null) throw new RuntimeException("More than one table annotation found in class " + clazz.getName());
                tableName = ((Table) a ).name();
            }
        }
        if(tableName == null) throw new RuntimeException("No table annotation found in class " + clazz.getName());
    }

    /**
     * Add the object to Data Base
     * @param entity the object to add
     */
    public void add(T entity){
        try{
            DatabaseAdder.Add(entity, con);
        }catch (Exception e){
            throw new RuntimeException("ADD ERROR \n" + e.getMessage());
        }
    }

    /**
     * Delete all the questions satisfying the Filter
     * @param filterConditions filter conditions
     * @return number of rows deleted
     */
    public int delete(List<FilterCondition<TField>> filterConditions){
        String filterString = FilterBuilder.buildFilter(filterConditions);
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM "+tableName+" WHERE " + filterString)) {
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("DELETE ERROR \n" + e.getMessage());
        }
    }

    /**
     * Query the database with the filter
     * @param filterConditions filter conditions
     * @return List of objects that are included in the filter converted to objects
     */
    public List<T> query(List<FilterCondition<TField>> filterConditions){
        return query(filterConditions, null, false, null, null);
    }

    /**
     * Stronger version of basic query, @param = null means it should not be used
     * @param filterConditions Condition for the rows
     * @param orderByField Field it should order by
     * @param ascending If it should be ascending or not
     * @param limit Max amount of rows selected
     * @param offset Offset no the rows
     * @return List of affected rows
     */
    public List<T> query(List<FilterCondition<TField>> filterConditions, TField orderByField, Boolean ascending, Integer limit, Integer offset){
       List<T> list = new ArrayList<>();
       String filterString = FilterBuilder.buildFilter(filterConditions);
       StringBuilder builder = new StringBuilder("SELECT * FROM "+tableName+" WHERE "+filterString);
       if(orderByField != null){
           builder.append(" ORDER BY "+orderByField.getColumnName());
           if(ascending) builder.append(" ASC");
           else builder.append(" DESC");
       }

       if(limit != null) builder.append(" LIMIT ").append(limit);
       if(offset != null) builder.append(" OFFSET ").append(offset);



       try(PreparedStatement stmt = con.prepareStatement(builder.toString())){
            ResultSet rs = stmt.executeQuery();

            // loop over the query result rows and add them to the list
            while(rs.next()) list.add((T) Converter.convert(clazz, rs));

            rs.close();
       }catch (Exception e){
           throw new RuntimeException("QUERY ERROR \n" + e.getMessage());
       }
       return list;

    }

    /**
     * Used for custom logic after SELECT * FROM table_name. Use this if you cant use other functions.
      * @param afterSelect what will be added after "SELECT * FROM table_name ";
     * @return affected rows in table
     */
    public List<T> query(String afterSelect){
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM "+tableName + " " + afterSelect;

        try(PreparedStatement stmt = con.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();

            // loop over the query result rows and add them to the list
            while(rs.next()) list.add((T) Converter.convert(clazz, rs));

            rs.close();
        }catch (Exception e){
            throw new RuntimeException("QUERY ERROR \n" + e.getMessage());
        }
        return list;

    }

    // Same Functions but for easier testing
    public List<T> query(FilterCondition<TField>... filter){
        return query(List.of(filter));
    }
    public int delete(FilterCondition<TField>... filter){
        return delete(List.of(filter));
    }

}
