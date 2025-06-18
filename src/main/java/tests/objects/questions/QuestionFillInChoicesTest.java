package tests.objects.questions;

import com.google.gson.JsonObject;
import objects.questions.Answer;
import objects.questions.QuestionFillInChoices;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionFillInChoicesTest {

    @Test
    void testAllCorrectAnswers() {
        List<Integer> correctIndexes = Arrays.asList(1, 0, 2);
        List<List<String>> choices = new ArrayList<>();
        choices.add(Arrays.asList("red", "blue"));
        choices.add(Arrays.asList("cat", "dog"));
        choices.add(Arrays.asList("one", "two", "three"));
        List<Integer> fillIndexes = Arrays.asList(5, 10, 15);

        QuestionFillInChoices question = new QuestionFillInChoices("Fill in the blanks", correctIndexes, choices, fillIndexes);

        Answer<Integer> answer = new Answer<>(Arrays.asList(1, 0, 2));

        int score = question.check(answer);

        assertEquals(3, score);
    }

    @Test
    void testSomeCorrectAnswers() {
        List<Integer> correctIndexes = Arrays.asList(1, 0, 2);
        List<List<String>> choices = Arrays.asList(
                Arrays.asList("red", "blue"),
                Arrays.asList("cat", "dog"),
                Arrays.asList("one", "two", "three")
        );
        List<Integer> fillIndexes = Arrays.asList(5, 10, 15);

        QuestionFillInChoices question = new QuestionFillInChoices("Partial", correctIndexes, choices, fillIndexes);

        Answer<Integer> answer = new Answer<>(Arrays.asList(1, 1, 2));

        int score = question.check(answer);

        assertEquals(2, score);
    }

    @Test
    void testAllIncorrectAnswers() {
        List<Integer> correctIndexes = Arrays.asList(0, 1, 2);
        List<List<String>> choices = Arrays.asList(
                Arrays.asList("A", "B"),
                Arrays.asList("C", "D"),
                Arrays.asList("E", "F", "G")
        );
        List<Integer> fillIndexes = Arrays.asList(3, 6, 9);

        QuestionFillInChoices question = new QuestionFillInChoices("Wrong", correctIndexes, choices, fillIndexes);

        Answer<Integer> answer = new Answer<>(Arrays.asList(1, 0, 0));

        int score = question.check(answer);

        assertEquals(0, score);
    }

    @Test
    void testEmptyAnswer() {
        List<Integer> correctIndexes = Arrays.asList(0, 1);
        List<List<String>> choices = Arrays.asList(
                Arrays.asList("yes", "no"),
                Arrays.asList("true", "false")
        );
        List<Integer> fillIndexes = Arrays.asList(1, 2);

        QuestionFillInChoices question = new QuestionFillInChoices("Empty", correctIndexes, choices, fillIndexes);

        Answer<Integer> answer = new Answer<>(new ArrayList<>());

        int score = question.check(answer);

        assertEquals(0, score);
    }

    @Test
    void testAnswerLongerThanCorrect() {
        List<Integer> correctIndexes = Arrays.asList(1, 0);
        List<List<String>> choices = Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList("c", "d")
        );
        List<Integer> fillIndexes = Arrays.asList(2, 4);

        QuestionFillInChoices question = new QuestionFillInChoices("Too long", correctIndexes, choices, fillIndexes);

        Answer<Integer> answer = new Answer<>(Arrays.asList(1, 0, 1, 1));

        int score = question.check(answer);

        assertEquals(2, score);
    }


    @Test
    void testGetDataAndPutData() {
        List<Integer> correctIndexes = Arrays.asList(1, 2);
        List<List<String>> choices = Arrays.asList(
                Arrays.asList("yes", "no"),
                Arrays.asList("maybe", "sure", "never")
        );
        List<Integer> fillIndexes = Arrays.asList(7, 14);

        QuestionFillInChoices original = new QuestionFillInChoices("Restore", correctIndexes, choices, fillIndexes);

        JsonObject jsonData = original.getData();

        QuestionFillInChoices restored = new QuestionFillInChoices(1, 1, "Restore", "", 2, jsonData);
        restored.putData(jsonData);

        assertEquals(2, restored.check(new Answer<>(Arrays.asList(1, 2))));
        assertEquals(0, restored.check(new Answer<>(Arrays.asList(0, 1))));
    }
}

