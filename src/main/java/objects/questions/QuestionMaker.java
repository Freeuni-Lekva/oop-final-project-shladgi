package objects.questions;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class QuestionMaker {

    public static Question makeQuestion(Integer id, Integer quizId, String question, String imageLink, int maxScore, QType type, JsonObject json){

        if(type == QType.SingleChoice) return new QuestionSingleChoice(id, quizId, question, imageLink, maxScore, json);
        //if(type == QType.MultiChoice) return new QuestionMultiChoice(id, quizId, question, imageLink, maxScore, json);
        //if(type == QType.TextAnswer) return new QuestionTextAnswer(id, quizId, question, imageLink, maxScore, json);
        //if(type == QType.MultiTextAnswer) return new QuestionMultiTextAnswer(id, quizId, question, imageLink, maxScore, json);
        if(type == QType.FillInBlanks) return new QuestionFillInBlanks(id, quizId, question, imageLink, maxScore, json);
        //if(type == QType.FillChoices) return new QuestionFillChoices(id, quizId, question, imageLink, maxScore, json);

        System.out.println("UNKNOWN QUESTION TYPE DETECTED IN QUESTIONMAKER" + type);
        return null;
    }
}
