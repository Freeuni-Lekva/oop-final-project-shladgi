package tests.objects.questions;

import objects.questions.Answer;
import objects.questions.QuestionMultiTextAnswer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class QuestionMultiTextAnswerTest {

    private QuestionMultiTextAnswer makeQuestion(List<List<String>> correct, boolean exact, boolean ordered) {
        return new QuestionMultiTextAnswer("?", correct, exact, ordered);
    }

    private Answer<String> makeAnswer(String... responses) {
        List<String> list = new ArrayList<>();
        for (String s : responses) {
            list.add(s);
        }
        Answer<String> a = new Answer<>(list);
        return a;
    }

    @Test
    public void testExactUnorderedAllCorrect() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));
        correct.add(List.of("Berlin"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, false);
        Answer<String> answer = makeAnswer("Berlin", "Paris");

        assertEquals(2, question.check(answer));
    }

    @Test
    public void testExactUnorderedPartialCorrect() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));
        correct.add(List.of("Berlin"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, false);
        Answer<String> answer = makeAnswer("Paris", "Rome");

        assertEquals(1, question.check(answer));
    }

    @Test
    public void testExactUnorderedNoMatch() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));
        correct.add(List.of("Berlin"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, false);
        Answer<String> answer = makeAnswer("London", "Rome");

        assertEquals(0, question.check(answer));
    }

    @Test
    public void testFuzzyUnorderedIgnoreSpacesCase() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("New York"));
        correct.add(List.of("San Francisco"));

        QuestionMultiTextAnswer question = makeQuestion(correct, false, false);
        Answer<String> answer = makeAnswer("newyork", "san  francisco");

        assertEquals(2, question.check(answer));
    }

    @Test
    public void testExactOrderedAllCorrect() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));
        correct.add(List.of("Berlin"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, true);
        Answer<String> answer = makeAnswer("Paris", "Berlin");

        assertEquals(2, question.check(answer));
    }

    @Test
    public void testExactOrderedWrongOrder() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));
        correct.add(List.of("Berlin"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, true);
        Answer<String> answer = makeAnswer("Berlin", "Paris");

        assertEquals(0, question.check(answer));
    }

    @Test
    public void testFuzzyOrderedMixedMatch() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("new york"));
        correct.add(List.of("san francisco"));

        QuestionMultiTextAnswer question = makeQuestion(correct, false, true);
        Answer<String> answer = makeAnswer("  NewYork", "SanFrancisco ");

        assertEquals(2, question.check(answer));
    }

    @Test
    public void testTooManyAnswers() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, true);
        Answer<String> answer = makeAnswer("Paris", "Berlin");

        assertEquals(1, question.check(answer));
    }

    @Test
    public void testEmptyAnswerList() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("Paris"));

        QuestionMultiTextAnswer question = makeQuestion(correct, true, false);
        Answer<String> answer = makeAnswer("");

        assertEquals(0, question.check(answer));
    }

    @Test
    public void testNoCorrectAnswers() {
        List<List<String>> correct = new ArrayList<>();

        QuestionMultiTextAnswer question = makeQuestion(correct, true, false);
        Answer<String> answer = makeAnswer("Paris");

        assertEquals(0, question.check(answer));
    }

    @Test
    public void testMultipleValidOptionsPerSlot() {
        List<List<String>> correct = new ArrayList<>();
        correct.add(List.of("NYC", "New York"));
        correct.add(List.of("SF", "San Francisco"));

        QuestionMultiTextAnswer question = makeQuestion(correct, false, true);
        Answer<String> answer = makeAnswer("new york", "sf");

        assertEquals(2, question.check(answer));
    }

}

