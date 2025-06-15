package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionTextAnswer extends Question {
    private boolean oneAns;
    private String correctAnswer;
    private List<String> correctAnswers;

    public QuestionTextAnswer(String question, String correctAnswer) {
        this.oneAns = true;
        this.question = question;
        this.correctAnswer = correctAnswer.toLowerCase();
        this.correctAnswers = new ArrayList<>();
        this.correctAnswers.add(this.correctAnswer);
    }

    public QuestionTextAnswer(String question, List<String> correctAnswers) {
        this.oneAns = false;
        this.question = question;
        this.correctAnswers = new ArrayList<>();
        for (String s : correctAnswers) {
            this.correctAnswers.add(s.toLowerCase());
        }
    }

    @Override
    public int check(Answer<?> answer) {
        if (oneAns) return answer.get().toString().toLowerCase().equals(correctAnswer) ? 1 : 0;
        List<String> remaining = new ArrayList<>(correctAnswers);
        for (int i = 0; i < answer.getSize(); i++) {
            String userAnswer = answer.get(i).toString().toLowerCase();
            if (!remaining.remove(userAnswer)) return 0;
        }
        return 1;
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
        for (int i = 0; i < jsonArray.size(); i++) {
            this.correctAnswers.add(jsonArray.get(i).getAsString().toLowerCase());
        }
        this.oneAns = correctAnswers.size() == 1;
        if (oneAns) {
            this.correctAnswer = correctAnswers.get(0);
        }
    }
}
