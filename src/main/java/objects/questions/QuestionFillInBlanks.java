package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.*;

public class QuestionFillInBlanks extends Question{

    // indecies where the blanks are.
    ArrayList<Integer> blankIdx;

    // list of possible correct answers for each blank
    ArrayList<ArrayList<String>> correctAnswers;

    // it is true if user's answer should match exactly with possible correct answer, otherwise false.
    boolean exactMatch;

    // first constructor where user gives me what QuestionFillInBlanks needs.
    // gives me whole question as a string
    // indexes of where the blanks start in the string
    // possible correct answers with appropriate sorting
    // if the correct and user's answers should be exactly same
    public QuestionFillInBlanks(String question, ArrayList<Integer> blankIdx, ArrayList<ArrayList<String>> correctAnswers, boolean exactMatch){
        type = QType.FillInBlanks;
        maxScore = blankIdx.size();
        this.question = question;

        this.blankIdx = blankIdx;
        this.correctAnswers = correctAnswers;
        this.exactMatch = exactMatch;
    }



    // to do in superClass
    /*public QuestionFillInBlanks(String question){
        type = QType.FillInBlanks;
        maxScore = blankIdx.size();
        this.question = question;
    }*/



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
        for (ArrayList<String> possibleAnswers : correctAnswers){
            JsonArray jsonPossibleAnswers = new JsonArray();

            for (String  possibleAnswer : possibleAnswers){
                jsonPossibleAnswers.add(possibleAnswer);
            }

            jsonCorrectAnswers.add(jsonPossibleAnswers);
        }
        json.add("correctAnswers", jsonCorrectAnswers);


        json.addProperty("exactMatch", exactMatch);

        return json;
    }

    // may be deleted.
    @Override
    protected void putData(JsonObject json) {
        // load blankIdx
        // blankIdx = new  ArrayList<>();

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

}
