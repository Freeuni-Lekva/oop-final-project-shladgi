package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionTextAnswer extends Question {
    private boolean oneAns, exactMatch;
    private String correctAnswer;
    private List<String> correctAnswers;

    public QuestionTextAnswer(String question, List<String> correctAnswers, boolean exactMatch) {
        this.oneAns = false;
        this.type = QType.TextAnswer;
        this.maxScore = 1;
        this.question = question;
        this.correctAnswers = new ArrayList<>();
        for (String s : correctAnswers) {
            this.correctAnswers.add(s.toLowerCase());
        }
    }

    @Override
    public int check(Answer<?> answer) {
        if (oneAns) return answer.get().toString().toLowerCase().equals(correctAnswer) ? 1 : 0;
        for(int i = 0; i < correctAnswers.size(); i++)
            if(myEquals((String) answer.get(), correctAnswers.get(i)))return 1;
        return 0;
    }

    @Override
    public JsonObject getData() {
        JsonObject json = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (String s : correctAnswers) jsonArray.add(s);
        json.add("correctAnswers", jsonArray);
        return json;
    }

    @Override
    public void putData(JsonObject json) {
        this.correctAnswers = new ArrayList<>();
        JsonArray jsonArray = json.getAsJsonArray("correctAnswers");
        for (int i = 0; i < jsonArray.size(); i++)
            this.correctAnswers.add(jsonArray.get(i).getAsString());

        this.oneAns = correctAnswers.size() == 1;
        if (oneAns) this.correctAnswer = correctAnswers.get(0);
    }

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
