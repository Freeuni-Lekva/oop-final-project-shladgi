package objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionFillInChoices extends Question{
    // this shows the correct index of answer for each blank space
    private List<Integer> correctIndexes;
    // list of answers for each blank
    private List<List<String>> choices;
    // indexes where blanks should be inserted
    private List<Integer> fillIndexes;

    public QuestionFillInChoices(String question, List<Integer> correctIndexes, List<List<String>> choices, List<Integer> fillIndexes){
        this.question = question;
        this.type = QType.FillChoices;
        this.maxScore = correctIndexes.size();
        this.correctIndexes = new ArrayList<>(correctIndexes);
        this.choices = new ArrayList<>();
        for(int i = 0; i < choices.size(); i++){
            this.choices.add(new ArrayList<>(choices.get(i)));
        }
        this.fillIndexes = new ArrayList<>(fillIndexes);
    }

    // this is a constructor that constructs the object from database table information.
    public QuestionFillInChoices(int id, int quizId, String question, String imageLink, int maxScore, double weight, JsonObject json) {
        super(id,quizId,question,imageLink,maxScore, weight,json, QType.FillChoices);
    }

    public QuestionFillInChoices() {

    }

    @Override
    public int check(Answer<?> answer) {
        if(answer.getSize() == 0) return 0;
        int points = 0;
        for(int i = 0; i < Math.min(this.correctIndexes.size(), answer.getSize()); i++){
            if(this.correctIndexes.get(i) == (int)answer.get(i)) points++;
        }
        return points;
    }

    @Override
    public void hideAnswers() {
        correctIndexes = null;
    }

    @Override
    public JsonObject getData() {
        JsonObject jsonObject = new JsonObject();

        JsonArray jsonCorrectIndexes = new JsonArray();
        for(Integer index : this.correctIndexes){
            jsonCorrectIndexes.add(index);
        }

        jsonObject.add("correctIndexes", jsonCorrectIndexes);

        JsonArray jsonChoices = new JsonArray();

        for(int i = 0; i < this.choices.size(); i++){
            JsonArray eachBlank = new JsonArray();

            for(int j = 0; j < this.choices.get(i).size(); j++){
                eachBlank.add(this.choices.get(i).get(j));
            }

            jsonChoices.add(eachBlank);
        }

        jsonObject.add("choices", jsonChoices);

        JsonArray jsonBlanks = new JsonArray();

        for(int i = 0; i < this.fillIndexes.size(); i++){
            jsonBlanks.add(this.fillIndexes.get(i));
        }

        jsonObject.add("blanks", jsonBlanks);

        return jsonObject;
    }

    @Override
    public void putData(JsonObject json) {
        this.correctIndexes = new ArrayList<>();
        JsonArray jsonCorrectIndexes = json.get("correctIndexes").getAsJsonArray();
        for(int i = 0; i < jsonCorrectIndexes.size(); i++){
            this.correctIndexes.add((Integer)jsonCorrectIndexes.get(i).getAsInt());
        }

        this.choices = new ArrayList<>();
        JsonArray jsonChoices = json.get("choices").getAsJsonArray();
        for(int i = 0; i < jsonChoices.size(); i++){
            JsonArray jsonChoice = jsonChoices.get(i).getAsJsonArray();
            List<String> choice = new ArrayList<>();
            for(int j = 0; j < jsonChoice.size(); j++){
                choice.add(jsonChoice.get(j).getAsString());
            }
            this.choices.add(choice);
        }

        this.fillIndexes = new ArrayList<>();

        JsonArray jsonBlanks = json.get("blanks").getAsJsonArray();

        for(int i = 0; i < jsonBlanks.size(); i++){
            fillIndexes.add((Integer)jsonBlanks.get(i).getAsInt());
        }

    }
}
