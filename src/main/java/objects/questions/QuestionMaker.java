package objects.questions;

import com.google.gson.JsonObject;

import java.util.ArrayList;

public class QuestionMaker {

    public static Question makeQuestion(QType type){
        if(type == QType.SingleChoice) return new QuestionSingleChoice();
        if(type == QType.MultiChoice) return new QuestionMultiChoice();
        if(type == QType.TextAnswer) return new QuestionTextAnswer();
        if(type == QType.MultiTextAnswer) return new QuestionMultiTextAnswer();
        if(type == QType.FillInBlanks) return new QuestionFillInBlanks();
        if(type == QType.FillChoices) return new QuestionFillInChoices();

        throw new RuntimeException("UNKNOWN QUESTION TYPE DETECTED IN QUESTIONMAKER" + type);
    }

    public static Question makeQuestion(Integer id, Integer quizId, String question, String imageLink, int maxScore, double weight, QType type, JsonObject json){


        if(type == QType.SingleChoice) return new QuestionSingleChoice(id, quizId, question, imageLink, maxScore, weight, json);
        if(type == QType.MultiChoice) return new QuestionMultiChoice(id, quizId, question, imageLink, maxScore, weight, json);
        if(type == QType.TextAnswer) return new QuestionTextAnswer(id, quizId, question, imageLink, maxScore, weight, json);
        if(type == QType.MultiTextAnswer) return new QuestionMultiTextAnswer(id, quizId, question, imageLink, maxScore, weight, json);
        if(type == QType.FillInBlanks) return new QuestionFillInBlanks(id, quizId, question, imageLink, maxScore, weight, json);
        if(type == QType.FillChoices) return new QuestionFillInChoices(id, quizId, question, imageLink, maxScore, weight, json);

        // if none of the types matched we messed up somewhere
        throw new RuntimeException("UNKNOWN QUESTION TYPE DETECTED IN QUESTIONMAKER" + type);
    }
}
