package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class QuestionSingleChoice extends Question{

    int correctId;
    ArrayList<String> choices;

    public QuestionSingleChoice(int correctId, ArrayList<String> choices) {
        this.correctId = correctId;
        this.choices = choices;
    }


    @Override
    public int check(Answer<?> answer) {
        if((Integer) answer.get() == correctId) return 1;
        return 0;
    }

    @Override
    public JsonObject getData() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("correctId", correctId);

        JsonArray choicesJson = new JsonArray();
        for (String choice : choices) choicesJson.add(choice);
        jsonObject.add("choices", choicesJson);

        return jsonObject;
    }

    @Override
    public void putData(JsonObject json) {
        correctId = json.get("correctId").getAsInt();

        choices.clear();
        JsonArray choicesJson = new JsonArray();
        for(JsonElement choice : choicesJson) choices.add(choice.getAsString());
    }
}
