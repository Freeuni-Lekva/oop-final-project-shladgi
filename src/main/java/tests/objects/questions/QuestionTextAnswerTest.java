package tests.objects.questions;

import com.google.gson.JsonObject;
import objects.questions.Answer;
import objects.questions.QType;
import objects.questions.Question;
import objects.questions.QuestionTextAnswer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QuestionTextAnswerTest {
    private QuestionTextAnswer questionTextAnswer;

    @Test
    public void testQuestionTextAnswerExactMatch() {
        List<String> answers = new ArrayList<>();
        answers.add("test");
        answers.add("test1");

        Answer<String> answer = new Answer<>(List.of("test"));
        Answer<String> answer1 = new Answer<>(List.of("tes"));
        questionTextAnswer = new QuestionTextAnswer("abc", answers,true);
        assertEquals(1, questionTextAnswer.check(answer));
        assertEquals(0,  questionTextAnswer.check(answer1));
    }

    @Test
    public void testQuestionTextAnswerNonExactMatch() {
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add("test");
        correctAnswers.add("test1");

        questionTextAnswer = new QuestionTextAnswer("abc", correctAnswers, false);

        Answer<String> answer1 = new Answer<>(List.of(" test "));
        Answer<String> answer2 = new Answer<>(List.of("TEST1"));
        Answer<String> answer3 = new Answer<>(List.of("es"));

        assertEquals(1, questionTextAnswer.check(answer1));
        assertEquals(1, questionTextAnswer.check(answer2));
        assertEquals(0, questionTextAnswer.check(answer3));
    }


    @Test
    public void testSecondConstructor() {
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add("test");
        correctAnswers.add("test1");

        Answer<String> answer = new Answer<>(List.of("test"));

        questionTextAnswer = new QuestionTextAnswer("abc", correctAnswers, true);
        JsonObject jsonObject = questionTextAnswer.getData();
        assertEquals(1, questionTextAnswer.check(answer));

        questionTextAnswer = new QuestionTextAnswer(0, 0, "abc", "link", 1, jsonObject);

        JsonObject loadedJson = questionTextAnswer.getData();
        assertEquals(jsonObject.toString(), loadedJson.toString());
        assertEquals(1, questionTextAnswer.check(answer));
    }

}
