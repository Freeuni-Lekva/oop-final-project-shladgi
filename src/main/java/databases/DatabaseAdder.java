package databases;

import com.google.gson.JsonObject;
import databases.annotations.AnnotationUtil;
import databases.annotations.Column;
import databases.annotations.HasJson;
import databases.annotations.Table;
import objects.ObjectWithJson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;

// This Class adds the Object to the table with database Connection
public class DatabaseAdder {

    /**
     * Adds the Object to its table, the Object must have @Table annotation
     * @param obj The Object you want to add
     * @param con Connection to the database where the table for this object is stored at
     */
    public static void Add(Object obj, Connection con){
        Class<?> clazz = obj.getClass();
        List<Annotation> classAnnotations = AnnotationUtil.getClassAnnotationsWithAncestors(clazz);

        // check if the class has the table ans hasJson annotation
        String tableName = null;
        String jsonColumnName = null;
        for(Annotation a : classAnnotations){
            if(a.annotationType().equals(Table.class)){
                if(tableName != null) throw new RuntimeException("More than one table annotation found in class " + clazz.getName());
                tableName = ((Table) a).name();
            }else if(a.annotationType().equals(HasJson.class)){
                if(jsonColumnName != null) throw new RuntimeException("More than one json column annotation found in class " + clazz.getName());
                jsonColumnName = ((HasJson) a).name();
            }
        }

        if(tableName == null) throw new RuntimeException("No table annotation found in class " + clazz.getName());

        List<Field> fields = AnnotationUtil.getFieldsWithAnnotationsWithAncestors(clazz, Column.class);
        ResultSet rs = null;
        PreparedStatement stmt = null;
        try{
            // StringBuilders for correct SQL column names and their correspondint values
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();


            Field primaryField = null; // for storing the PrimaryKey (we have to get it later because when inserting, the primary key is still unknown)
            String primaryKeyName = null;
            for(Field f : fields){
                Column a = f.getAnnotation(Column.class);
                if(a == null) continue; // if no @Column annotation skip
                if(a.primary()){ // if it is primaryKey save it for later as we will need to update it
                    if(primaryField != null) throw new RuntimeException("More than one primary column found in class " + clazz.getName());
                    primaryField = f;
                    primaryKeyName = a.name();
                    continue;
                }
                String columnName = f.getAnnotation(Column.class).name();
                addField(f, obj, columns, values, columnName); // add the field to the StringBuilders
            }
            if(primaryField == null) throw new RuntimeException("No primary column found in class " + clazz.getName());

            // add json data if we have it it we do the object must implement the ObjectWithJson interface
            if(jsonColumnName != null){
                if(!ObjectWithJson.class.isAssignableFrom(clazz))
                    throw new RuntimeException("Class " + clazz.getName() + " does not implement ObjectWithJson While it has a json column annotation");
                columns.append(jsonColumnName).append(", ");
                String json = ((ObjectWithJson) obj).getData().toString();
                values.append("'").append(json).append("', ");
            }

            // remove extra ", " after all the values and columns are set
            columns.setLength(columns.length()-2);
            values.setLength(values.length()-2);

            // correctly format the SQL string
            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);

            // create a statement that will also store the keys it generated. this will be useful
            stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys(); // get the keys generated
            if(rs.next()){ // set the id of the object to the generated value
                primaryField.setAccessible(true);
                primaryField.set(obj, rs.getInt(primaryKeyName));
            }
        }catch (Exception e){
            throw new RuntimeException("ERROR IN ADD FUNCTION IN DATABASE " + e.getMessage());
        }finally {
            try{
                if(rs != null) rs.close();
                if(stmt != null) stmt.close();
            }catch (Exception e){
                throw new RuntimeException("COULD NOT CLOSE resultset or statement" + e.getMessage());
            }
        }
    }


    /**
     * Adds the fields column name and its value to corresponding StringBuilders
     * @param f The field you want to add
     * @param obj The object with this field
     * @param columns StringBuilder storing the columns
     * @param values StringBuilder storing the values
     * @param columnName The name of the column
     * @throws IllegalAccessException
     */
    private static void addField(Field f, Object obj, StringBuilder columns, StringBuilder values, String columnName) throws IllegalAccessException {
        f.setAccessible(true);
        Class<?> type = f.getType(); // get the field type
        columns.append(columnName).append(", ");
        Object val = f.get(obj); // the value this field has, this will be stored in the values StringBuilder, but needs some proccessing depending on the type
        if(val == null){
            values.append("NULL, ");
            return;
        }
        if(type == Integer.class || type == int.class){
            values.append(val).append(", ");
        }else if(type == String.class){
            values.append("'").append(val).append("', ");
        }else if(type == boolean.class || type == Boolean.class){
            values.append(val).append(", ");
        }else if(type == Double.class || type == double.class){
            values.append(val).append(", ");
        }else if(type == LocalDateTime.class || type == java.sql.Timestamp.class || type == java.sql.Date.class || type == java.util.Date.class){
            values.append("'").append(val).append("', ");
        }else if(type.isEnum()){
            values.append("'").append(val).append("', ");
        }else{
            throw new RuntimeException("ERROR IN ADD FIELD FUNCTION IN DATABASE " + f.getName() + " " + f.getType().getName());
        }
    }
}
