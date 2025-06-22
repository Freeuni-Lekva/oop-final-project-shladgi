package objects;

import com.google.gson.JsonObject;

public interface ObjectWithJson {
    // json storing for database
    // generates json from the data that is specific to a given class
    public JsonObject getData();

    // given the json data generated for this object. store it in this object
    // variables
    public void putData(JsonObject json);
}
