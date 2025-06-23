package databases.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


// for classes that also have things stored in json like question classes which store data with json
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HasJson {
    String name();
}
