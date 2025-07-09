package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionMultiTextAnswer extends Question{
    List<List<String>> correctAnswers;
    boolean exactMatch;
    boolean ordered;

    public QuestionMultiTextAnswer(String question, List<List<String>> correctAnswers, boolean exactMatch, boolean ordered){
        this.question = question;
        this.ordered = ordered;
        this.type = QType.MultiTextAnswer;
        this.correctAnswers = new ArrayList<>();
        for(int i = 0; i < correctAnswers.size(); i++){
            this.correctAnswers.add(new ArrayList<>(correctAnswers.get(i)));
        }
        this.exactMatch = exactMatch;
    }


    public QuestionMultiTextAnswer(int quizId, String question, List<List<String>> correctAnswers, boolean exactMatch, boolean ordered, String imageLink, double weight){
        this.question = question;
        this.ordered = ordered;

        this.type = QType.MultiTextAnswer;

        this.correctAnswers = new ArrayList<>();
        for(int i = 0; i < correctAnswers.size(); i++){
            this.correctAnswers.add(new ArrayList<>(correctAnswers.get(i)));
        }
        this.exactMatch = exactMatch;

        this.quizId = quizId;
        this.imageLink = imageLink;
        this.weight = weight;
    }


    // this is a constructor that constructs the object from database table information.
    public QuestionMultiTextAnswer(int id, int quizId, String question, String imageLink, int maxScore, double weight, JsonObject json) {
        super(id,quizId,question,imageLink,maxScore, weight,json, QType.MultiTextAnswer);    }

    public QuestionMultiTextAnswer() {

    }

    @Override
    public int check(Answer<?> answer) {
        if(answer.getSize() == 0) return 0;
        if(this.ordered) return orderedCheck(answer);

        if (!(answer.get(0) instanceof String)) return 0;

        List<String> userAnswers = new ArrayList<>();
        for (int i = 0; i < answer.getSize(); i++) {
            userAnswers.add((String) answer.get(i));
        }

        boolean[] used = new boolean[correctAnswers.size()];
        int points = 0;

        for (String userAnswer : userAnswers) {
            boolean matched = false;

            for (int i = 0; i < correctAnswers.size(); i++) {
                if (used[i]) continue;

                for (String possible : correctAnswers.get(i)) {
                    if (myCheck(possible, userAnswer)) {
                        used[i] = true;
                        points++;
                        matched = true;
                        break;
                    }
                }

                if (matched) break;
            }
        }

        return points;
    }

    @Override
    public void hideAnswers() {
        correctAnswers = null;
    }

    private int orderedCheck(Answer<?> answer) {
        if (!(answer.get(0) instanceof String)) return 0;

        int points = 0;
        int minSize = Math.min(answer.getSize(), correctAnswers.size());

        for (int i = 0; i < minSize; i++) {
            String userAnswer = ((String) answer.get(i));

            for (String possible : correctAnswers.get(i)) {
                if (myCheck(possible, userAnswer)) {
                    points++;
                    break;
                }
            }
        }

        return points;
    }


    private boolean myCheck(String possibleAnswer, String answer){
        if(this.exactMatch){
            return possibleAnswer.equals(answer);
        }

        answer = answer.toLowerCase();
        // \\s+ is equals to " ".
        answer = answer.replaceAll("\\s+","");

        possibleAnswer = possibleAnswer.toLowerCase();
        possibleAnswer = possibleAnswer.replaceAll("\\s+","");

        return answer.equals(possibleAnswer);

    }

    @Override
    public JsonObject getData() {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("exactMatch", (Boolean) this.exactMatch);
        jsonObject.addProperty("ordered", (Boolean) this.ordered);

        JsonArray jsonArray = new JsonArray();
        for(int i = 0; i < this.correctAnswers.size(); i++){
            JsonArray each = new JsonArray();
            for(int j = 0; j < this.correctAnswers.get(i).size(); j++){
                each.add(correctAnswers.get(i).get(j));
            }
            jsonArray.add(each);
        }

        jsonObject.add("correctAnswers", jsonArray);

        return jsonObject;
    }

    @Override
    public void putData(JsonObject json) {
        this.exactMatch = json.get("exactMatch").getAsBoolean();
        this.ordered = json.get("ordered").getAsBoolean();
        JsonArray jsonArray = json.get("correctAnswers").getAsJsonArray();

        this.correctAnswers = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            JsonArray jsonEach = jsonArray.get(i).getAsJsonArray();
            List<String> each = new ArrayList<>();
            for(int j = 0; j < jsonEach.size(); j++){
                each.add(jsonEach.get(i).getAsString());
            }
            this.correctAnswers.add(each);
        }
    }
}
