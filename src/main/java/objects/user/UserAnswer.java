package objects.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import databases.annotations.Column;
import databases.annotations.HasJson;
import databases.annotations.Table;
import objects.ObjectWithJson;
import objects.questions.Answer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

@Table(name = "user_answers")
@HasJson(name = "jsondata")
public class UserAnswer implements ObjectWithJson {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "questionid")
    private int questionId;


    @Column(name = "resultid")
    private int resultId;

    @Column(name = "isstring")
    private boolean isString;

    private Answer<Integer> intAnswer;
    private Answer<String> strAnswer;

    public UserAnswer() {}

    public UserAnswer(int questionId, int resultId, boolean isString, Answer<?> ans ) {
        this.questionId = questionId;
        this.resultId = resultId;
        this.isString = isString;
        if(isString) this.strAnswer =(Answer<String>) ans;
        else this.intAnswer =(Answer<Integer>) ans;
    }

    @Override
    public JsonObject getData() {
        JsonObject json = new JsonObject();
        JsonArray jaChoices = new JsonArray();
        if(isString) for(int i = 0; i < strAnswer.getSize(); i++) jaChoices.add(strAnswer.get(i));
        else for(int i = 0; i < intAnswer.getSize(); i++) jaChoices.add(intAnswer.get(i));
        json.add("choices", jaChoices);
        return json;
    }

    @Override
    public void putData(JsonObject json) {
        JsonArray jaChoices = json.get("choices").getAsJsonArray();
        if(isString){
            List<String> ls = new ArrayList<>();
            for(JsonElement el : jaChoices) ls.add(el.getAsString());
            strAnswer = new Answer<>(ls);
        }else{
            List<Integer> ls = new ArrayList<>();
            for(JsonElement el : jaChoices) ls.add(el.getAsInt());
            intAnswer = new Answer<>(ls);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getResultId() {
        return resultId;
    }

    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    public boolean isString() {
        return isString;
    }

    public void setIsString(boolean isString) {
        this.isString = isString;
    }

    public Answer<Integer> getIntAnswer() {
        return intAnswer;
    }

    public void setIntAnswer(Answer<Integer> intAnswer) {
        this.intAnswer = intAnswer;
    }

    public Answer<String> getStrAnswer() {
        return strAnswer;
    }

    public void setStrAnswer(Answer<String> strAnswer) {
        this.strAnswer = strAnswer;
    }
}
