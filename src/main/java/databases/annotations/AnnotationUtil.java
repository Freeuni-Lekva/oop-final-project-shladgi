package databases.annotations;

import objects.questions.QuestionSingleChoice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {
    public static List<Annotation> getClassAnnotationsWithAncestors(Class<?> clazz){
        List<Annotation> annotations = new ArrayList<>();
        while(clazz != null && clazz != Object.class){
            Annotation[] classAnnotations = clazz.getDeclaredAnnotations();
            for(Annotation a : classAnnotations) annotations.add(a);
            clazz = clazz.getSuperclass();
        }
        return annotations;
    }

    public static List<Field> getFieldsWithAnnotationsWithAncestors(Class<?> clazz, Class<?>... Annotations){
        List<Field> fields = new ArrayList<>();
        while(clazz != null && clazz != Object.class){

            Field[] classFields = clazz.getDeclaredFields();


            for(Field f : classFields){
                Annotation[] fieldAnnotations = f.getDeclaredAnnotations();

                boolean found = false;
                // check if fields annotations have the anotations that we care about. if yes then add the field to the list
                for(Annotation a : fieldAnnotations){
                    for(Class<?> bClass : Annotations){
                        if(a.annotationType().equals(bClass)){
                            fields.add(f);
                            found = true;
                            break;
                        }
                    }
                    if(found) break;
                }

            }

            // go to the upper class
            clazz = clazz.getSuperclass();
        }
        return fields;
    }


    public static void main(String[] args) {
        getClassAnnotationsWithAncestors(QuestionSingleChoice.class);
        getFieldsWithAnnotationsWithAncestors(QuestionSingleChoice.class, Column.class);
    }

}
