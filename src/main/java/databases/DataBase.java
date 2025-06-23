package databases;

import databases.annotations.AnnotationUtil;
import databases.annotations.Table;
import databases.filters.Filter;
import databases.filters.FilterBuilder;
import databases.filters.FilterCondition;
import databases.filters.fields.SqlField;
import objects.questions.Question;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public abstract class DataBase<T, TField extends SqlField>{
    Connection con;
    Class<T> clazz;
    String tableName = null;

    public DataBase(Connection con, Class<T> clazz){
        this.con = con;
        this.clazz = clazz;
        initTable();
    }

    private void initTable(){
        List<Annotation> classAnnotations = AnnotationUtil.getClassAnnotationsWithAncestors(clazz);
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
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM "+tableName+" WHERE " + filterString)) {
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
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM "+tableName+" WHERE "+filterString)){
            ResultSet rs = stmt.executeQuery();

            // loop over the query result rows and add them to the list
            while(rs.next()) list.add((T) Converter.convert(clazz, rs));

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

}
