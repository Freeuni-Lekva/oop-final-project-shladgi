package databases;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import databases.annotations.AnnotationUtil;
import databases.annotations.Column;
import databases.annotations.HasJson;
import databases.annotations.Table;
import objects.ObjectWithJson;
import objects.questions.QType;
import objects.questions.Question;
import objects.questions.QuestionMaker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class Converter {
    public static Object convert(Class<?> clazz, ResultSet rs){
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
            // create the object (handle question objects differently
            Object obj = null;
            if(clazz == Question.class){
                String type = rs.getString("type");
                if(type == null) throw new RuntimeException("Question type is null");
                QType qType = QType.valueOf(type);
                obj = QuestionMaker.makeQuestion(qType);
            }else{
                obj = clazz.getDeclaredConstructor().newInstance();
            }

            if(jsonColumnName != null){
                if(ObjectWithJson.class.isAssignableFrom(clazz) == false)
                    new RuntimeException("Class " + clazz.getName() + " does not implement ObjectWithJson While it has a json column annotation");
                String jsonData = rs.getString(jsonColumnName);
                Gson gson = new Gson();
                JsonObject json = gson.fromJson(jsonData, JsonObject.class);
                ((ObjectWithJson) obj).putData(json);
            }
            for(Field f : fields){
                Column a = f.getAnnotation(Column.class);
                if(a == null) continue;
                setField(f, obj, rs, a.name());
            }
            return obj;
        }catch (Exception e){
            throw new RuntimeException("ERROR IN CONVERT FUNCTION IN DATABASE " + e.getMessage());
        }

    }

    private static void setField(Field f, Object obj, ResultSet rs, String columnName) throws IllegalAccessException, SQLException {
        f.setAccessible(true);
        Class<?> type = f.getType();
        if(type == int.class || type == Integer.class){
            f.set(obj, rs.getInt(columnName));
        }else if(type == String.class){
            f.set(obj, rs.getString(columnName));
        }else if(type == boolean.class || type == Boolean.class){
            f.set(obj, rs.getBoolean(columnName));
        }else if(type == Double.class || type == double.class){
            f.set(obj, rs.getDouble(columnName));
        }else if(type == LocalDateTime.class || type == java.sql.Timestamp.class || type == java.sql.Date.class || type == java.util.Date.class){
            f.set(obj, rs.getTimestamp(columnName).toLocalDateTime());
        }else if(type.isEnum()){
            f.set(obj, Enum.valueOf((Class<Enum>) type, rs.getString(columnName)));
        }else{
            throw new RuntimeException("ERROR IN SET FIELD FUNCTION IN DATABASE " + f.getName() + " " + f.getType().getName());
        }
    }
}
