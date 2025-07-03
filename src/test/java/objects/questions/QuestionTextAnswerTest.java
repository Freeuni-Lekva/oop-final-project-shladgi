package objects.questions;

import com.google.gson.JsonObject;
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
        answers.add("Test");

        Answer<String> answer = new Answer<>(List.of("test"));
        Answer<String> answer1 = new Answer<>(List.of("tes"));
        Answer<String> answer2 = new Answer<>(List.of("Test"));
        Answer<String> answer3 = new Answer<>(List.of("Test1"));
        questionTextAnswer = new QuestionTextAnswer("abc", answers,true);
        assertEquals(1, questionTextAnswer.check(answer));
        assertEquals(0,  questionTextAnswer.check(answer1));
        assertEquals(1,  questionTextAnswer.check(answer2));
        assertEquals(0,  questionTextAnswer.check(answer3));
    }

    @Test
    public void testQuestionTextAnswerNonExactMatch() {
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add("test1");
        correctAnswers.add(" T est ");

        questionTextAnswer = new QuestionTextAnswer("abc", correctAnswers, false);

        Answer<String> answer1 = new Answer<>(List.of(" test "));
        Answer<String> answer2 = new Answer<>(List.of("TEST1"));
        Answer<String> answer3 = new Answer<>(List.of("es"));
        Answer<String> answer4 = new Answer<>(List.of("tesT    1"));
        Answer<String> answer5 = new Answer<>(List.of("te    s1t"));

        assertEquals(1, questionTextAnswer.check(answer1));
        assertEquals(1, questionTextAnswer.check(answer2));
        assertEquals(0, questionTextAnswer.check(answer3));
        assertEquals(1, questionTextAnswer.check(answer4));
        assertEquals(0, questionTextAnswer.check(answer5));
    }


    @Test
    public void testSecondConstructorExactMatch() {
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add("Test");
        correctAnswers.add("test1");
        correctAnswers.add("T e     s   t   ");

        Answer<String> answer = new Answer<>(List.of("Test"));
        Answer<String> answer1 = new Answer<>(List.of("test"));
        Answer<String> answer2 = new Answer<>(List.of("T e     s   t   "));
        Answer<String> answer3 = new Answer<>(List.of("T e     s   t  "));
        questionTextAnswer = new QuestionTextAnswer("abc", correctAnswers, true);
        JsonObject jsonObject = questionTextAnswer.getData();
        assertEquals(1, questionTextAnswer.check(answer));

        questionTextAnswer = new QuestionTextAnswer(0, 0, "abc", "link", 1, 1.0, jsonObject);

        JsonObject loadedJson = questionTextAnswer.getData();
        assertEquals(jsonObject.toString(), loadedJson.toString());
        assertEquals(1, questionTextAnswer.check(answer));
        assertEquals(0, questionTextAnswer.check(answer1));
        assertEquals(1, questionTextAnswer.check(answer2));
        assertEquals(0, questionTextAnswer.check(answer3));
    }

    @Test
    public void testSecondConstructorNonExactMatch() {
        List<String> correctAnswers = new ArrayList<>();
        correctAnswers.add(" test ");
        correctAnswers.add(" T    est 1 ");

        Answer<String> answer = new Answer<>(List.of("test"));
        Answer<String> answer1 = new Answer<>(List.of("tesT"));
        Answer<String> answer2 = new Answer<>(List.of("TE     S T 1 "));
        Answer<String> answer3 = new Answer<>(List.of(" t es "));
        Answer<String> answer4 = new Answer<>(List.of("tesT."));

        questionTextAnswer = new QuestionTextAnswer("abc", correctAnswers, false);
        JsonObject jsonObject = questionTextAnswer.getData();
        assertEquals(1, questionTextAnswer.check(answer));

        questionTextAnswer = new QuestionTextAnswer(0, 0, "abc", "link", 1, 2.0, jsonObject);

        JsonObject loadedJson = questionTextAnswer.getData();
        assertEquals(jsonObject.toString(), loadedJson.toString());
        assertEquals(1, questionTextAnswer.check(answer));
        assertEquals(1, questionTextAnswer.check(answer1));
        assertEquals(1, questionTextAnswer.check(answer2));
        assertEquals(0, questionTextAnswer.check(answer3));
        assertEquals(0, questionTextAnswer.check(answer4));
    }
}
