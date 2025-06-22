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

public class DatabaseAdder {
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

        try{
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();

            Field primaryField = null;
            for(Field f : fields){
                Column a = f.getAnnotation(Column.class);
                if(a == null) continue;
                if(a.primary()){ // it it is primary save it for later as we want to save it for later
                    if(primaryField != null) throw new RuntimeException("More than one primary column found in class " + clazz.getName());
                    primaryField = f;
                    continue;
                }
                String columnName = f.getAnnotation(Column.class).name();
                addField(f, obj, columns, values, columnName);
            }
            if(primaryField == null) throw new RuntimeException("No primary column found in class " + clazz.getName());

            // add json data if we have it
            if(jsonColumnName != null){
                columns.append(jsonColumnName).append(", ");
                String json = ((ObjectWithJson) obj).getData().toString();
                values.append("'").append(json).append("', ");
            }

            // remove extra ", ";
            columns.setLength(columns.length()-2);
            values.setLength(values.length()-2);

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, values);

            PreparedStatement stmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()){
                primaryField.setAccessible(true);
                primaryField.set(obj, rs.getInt(1));
            }

            rs.close();
            stmt.close();

        }catch (Exception e){
            throw new RuntimeException("ERROR IN ADD FUNCTION IN DATABASE " + e.getMessage());
        }
    }

    private static void addField(Field f, Object obj, StringBuilder columns, StringBuilder values, String columnName) throws IllegalAccessException {
        f.setAccessible(true);
        Class<?> type = f.getType();
        columns.append(columnName).append(", ");
        Object val = f.get(obj);
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
