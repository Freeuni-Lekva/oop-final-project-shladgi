package databases.annotations;

import objects.questions.QuestionSingleChoice;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


// This is aclass that has utility functions for database annotations
public class AnnotationUtil {

    /**
     * returns the Class annotations of the given class and its ancestor classes
     * @param clazz is the Class you want to get the annotations for
     * @return List of annotations that the class and its parent classes have not including Object class
     */
    public static List<Annotation> getClassAnnotationsWithAncestors(Class<?> clazz){
        List<Annotation> annotations = new ArrayList<>();

        // goes up the ancestry tree and processes each of them
        while(clazz != null && clazz != Object.class){

            Annotation[] classAnnotations = clazz.getDeclaredAnnotations();

            for(Annotation a : classAnnotations) annotations.add(a);

            clazz = clazz.getSuperclass(); // get the parent class
        }

        return annotations;
    }

    /**
     * returns the Fields with the annotation you provide. Fields are searched in the given class and its ancestors
     * @param clazz is the Class you want to get the field annotations for
     * @param Annotations are the annotations you care for, if the field has an annotation but its not in this list it will get skipped
     * @return List of Fields of the class and its ancestor classes which have the Annotations you passed to the function
     */
    public static List<Field> getFieldsWithAnnotationsWithAncestors(Class<?> clazz, Class<?>... Annotations){
        List<Field> fields = new ArrayList<>();

        // processes the classes going up the ancestry tree
        while(clazz != null && clazz != Object.class){

            Field[] classFields = clazz.getDeclaredFields();

            // loop over fields and check if we care about it
            for(Field f : classFields){
                Annotation[] fieldAnnotations = f.getDeclaredAnnotations();

                boolean found = false;

                // check if fields annotations have the anotations that we care about. if yes then add the field to the list
                for(Annotation a : fieldAnnotations){
                    for(Class<?> bClass : Annotations){
                        if(a.annotationType().equals(bClass)){ // if it has the annotation we are looking for
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
}
