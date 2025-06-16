package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionMultiChoice extends Question{

    List<String> choices;
    //This saves indexes of correct answers
    List<Integer> correctChoices;
    //This should be true if all the answers should be correct to get points
    boolean exactMatch;


    public QuestionMultiChoice(String question, List<Integer> correctChoices, List<String> choices, boolean exactMatch) {
        this.type = QType.MultiChoice;
        this.maxScore = correctChoices.size();
        this.question = question;
        this.correctChoices = correctChoices;
        this.choices = choices;
        this.exactMatch = exactMatch;
    }

    // this is a constructor that constructs the object from database table information.
    public QuestionMultiChoice(int id, int quizId, String question, String imageLink, int maxScore, JsonObject json) {
        super(id,quizId,question,imageLink,maxScore,json, QType.SingleChoice);
    }

    @Override
    public int check(Answer<?> answer) {
        int points = getPoints(answer);
        if(exactMatch){
            if(points == correctChoices.size() &&
                    answer.getSize()== correctChoices.size())return points;
            else return 0;
        }
        return points;
    }

    @Override
    public JsonObject getData() {
        JsonObject jo = new JsonObject();

        JsonArray jaChoices = new JsonArray();
        for (String choice : choices) jaChoices.add(choice);
        jo.add(CH, jaChoices);

        JsonArray jaCorrectChoises = new JsonArray();
        for(Integer correctChoice : correctChoices) jaCorrectChoises.add(correctChoice);
        jo.add(CC, jaCorrectChoises);

        jo.addProperty(EM, exactMatch);

        return jo;
    }

    @Override
    protected void putData(JsonObject json) {
        exactMatch = json.get(EM).getAsBoolean();

        JsonArray jaChoices = json.get(CH).getAsJsonArray();
        choices= new ArrayList<>();
        for(JsonElement choice : jaChoices) choices.add(choice.getAsString());

        JsonArray jaCorrectChoises = json.get(CC).getAsJsonArray();
        correctChoices= new ArrayList<>();
        for (JsonElement choice : jaCorrectChoises) correctChoices.add(choice.getAsInt());

    }

    // For each correct answer player gets +1 point and -1 for every mistake.
    //
    private int getPoints(Answer<?> answer) {
        int countPoints = 0;
        for(int i =0;i<answer.getSize();i++){
            if(correctChoices.contains(answer.get(i))) {
                countPoints++;
            }else countPoints--;
        }
        return Math.max(0, countPoints);
    }

    private static String EM = "exactMatch";
    private static String CH = "choices";
    private static String CC = "correctChoices";

}