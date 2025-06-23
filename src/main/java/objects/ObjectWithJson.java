package objects;

import com.google.gson.JsonObject;

public interface ObjectWithJson {

    /**
     * Gets the object specific fata
     * @return JsonObject storing the object specific data
     */
    public JsonObject getData();

    /**
     * Stores the JsonObject data in the object, this must be the same kind of data that the GetData() function returned
     * @param json JsonData that was at once taken from this class with GetData()
     */
    public void putData(JsonObject json);
}
