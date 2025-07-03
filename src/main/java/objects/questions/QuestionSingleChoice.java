package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionSingleChoice extends Question{

    int correctId;  // correct answer index
    List<String> choices; // answer choices that the user will see
    public QuestionSingleChoice() {}
    /**
     * Constructor of QuestionSingleChoice
     * @param question Question string
     * @param correctId Index of the correct answer
     * @param choices List of choices that will be displayed do user
     */
    public QuestionSingleChoice(String question, int correctId, List<String> choices) {
        this.type = QType.SingleChoice;
        this.maxScore = 1;
        this.question = question;
        this.correctId = correctId;
        this.choices = new ArrayList<>(choices);
    }

    // this is a constructor that constructs the object from database table information.
    public QuestionSingleChoice(int id, int quizId, String question, String imageLink, int maxScore, double weight, JsonObject json) {
        super(id,quizId,question,imageLink,maxScore, weight,json, QType.SingleChoice);
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

        choices = new ArrayList<String>();
        JsonArray choicesJson = json.get("choices").getAsJsonArray();
        for(JsonElement choice : choicesJson) choices.add(choice.getAsString());
    }
}
