package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.*;

public class QuestionFillInBlanks extends Question{

    // indecies where the blanks are.
    List<Integer> blankIdx;

    // list of possible correct answers for each blank
    List<List<String>> correctAnswers;

    // it is true if user's answer should match exactly with possible correct answer, otherwise false.
    boolean exactMatch;

    // first constructor where user gives me what QuestionFillInBlanks needs.
    // gives me whole question as a string
    // indexes of where the blanks start in the string
    // possible correct answers with appropriate sorting
    // if the correct and user's answers should be exactly same
    public QuestionFillInBlanks(String question, List<Integer> blankIdx, List<List<String>> correctAnswers, boolean exactMatch){
        type = QType.FillInBlanks;
        this.question = question;

        //  amas awi agar viyenebt.
        // deep copy of blankIdx
        this.blankIdx = new ArrayList<>(blankIdx);

        this.correctAnswers = new ArrayList<>();
        for(List<String> arr : correctAnswers){
            this.correctAnswers.add(new ArrayList<>(arr)); // copy inner list
        }

        this.exactMatch = exactMatch;

        maxScore = this.blankIdx.size();
    }


    public QuestionFillInBlanks(int quizId, String question, List<Integer> blankIdx, List<List<String>> correctAnswers, boolean exactMatch, String photoUrl, double weight){
        type = QType.FillInBlanks;
        this.question = question;

        // deep copy of blankIdx
        this.blankIdx = new ArrayList<>(blankIdx);

        this.correctAnswers = new ArrayList<>();
        for(List<String> arr : correctAnswers){
            this.correctAnswers.add(new ArrayList<>(arr)); // copy inner list
        }

        this.exactMatch = exactMatch;

        maxScore = this.correctAnswers.size();

        this.quizId = quizId;
        this.imageLink = photoUrl;
        this.weight = weight;
    }



    // this is a constructor that constructs the object from database table information.
    public QuestionFillInBlanks(int id, int quizId, String question, String imageLink, int maxScore, double weight, JsonObject json) {
        super(id,quizId,question,imageLink,maxScore, weight,json, QType.FillInBlanks);
    }

    public QuestionFillInBlanks() {

    }

    // compare participant's answers with given possible correct answers.
    @Override
    public int check(Answer<?> answer) {
        int score = 0;
        for(int i = 0; i < correctAnswers.size(); i++){
            for(String correctAnswerStr : correctAnswers.get(i)){
                if(myEquals((String) answer.get(i), correctAnswerStr)){
                    score++;
                    break;
                }
            }
        }
        return score;
    }

    // user get data with jsonObject to add in DB.
    @Override
    public JsonObject getData() {
        JsonObject json = new JsonObject();

        JsonArray jsonBlanks = new JsonArray();
        for(Integer idx : blankIdx){
            jsonBlanks.add(idx);
        }

        json.add("blanks", jsonBlanks);

        JsonArray jsonCorrectAnswers = new JsonArray();
        for (List<String> possibleAnswers : correctAnswers){
            JsonArray jsonPossibleAnswers = new JsonArray();

            for (String  possibleAnswer : possibleAnswers){
                jsonPossibleAnswers.add(possibleAnswer);
            }

            jsonCorrectAnswers.add(jsonPossibleAnswers);
        }
        json.add("correctAnswers", jsonCorrectAnswers);


        json.addProperty("exactMatch", exactMatch);

        json.addProperty("maxScore", maxScore);

        return json;
    }


    @Override
    public void putData(JsonObject json) {
        // blanks
        blankIdx = new ArrayList<>();
        JsonArray jsonBlanks = json.getAsJsonArray("blanks");
        for(int i = 0; i <  jsonBlanks.size(); i++){
            blankIdx.add(jsonBlanks.get(i).getAsInt());
        }

        // correct answers
        correctAnswers = new ArrayList<>();

        // get full answers matrix
        JsonArray jsonCorrectAnswers = json.getAsJsonArray("correctAnswers");

        for(int i = 0; i <  jsonCorrectAnswers.size(); i++){
            // get each questions possible answers
            JsonArray jsonPossibleAnswers = jsonCorrectAnswers.get(i).getAsJsonArray();

            List<String> possibleAnswers = new ArrayList<>();

            // add from json to arrayList
            for(int j = 0; j <  jsonPossibleAnswers.size(); j++){
                possibleAnswers.add(jsonPossibleAnswers.get(j).getAsString());
            }
            // add array's to matrix
            correctAnswers.add(possibleAnswers);
        }

        exactMatch = json.get("exactMatch").getAsBoolean();

        maxScore = json.get("maxScore").getAsInt();
    }

    // i should compare string due to given exactMatch boolean.
    private boolean myEquals(String answer, String correctAnswer){
        if(exactMatch){
            return answer.equals(correctAnswer);
        }

        answer = answer.toLowerCase();
        // \\s+ is equals to " ".
        answer = answer.replaceAll("\\s+","");

        correctAnswer = correctAnswer.toLowerCase();
        correctAnswer = correctAnswer.replaceAll("\\s+","");

        return answer.equals(correctAnswer);
    }

    @Override
    public void hideAnswers(){
        correctAnswers = null;
    }
}
