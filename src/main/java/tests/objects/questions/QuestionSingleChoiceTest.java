package tests.objects.questions;

import objects.questions.Answer;
import objects.questions.QuestionSingleChoice;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionSingleChoiceTest {

    private static List<Answer<Integer>> answers;


    @BeforeAll
    public static void SetUp(){
        answers = new ArrayList<>();
        answers.add(new Answer<>(List.of(0)));
        answers.add(new Answer<>(List.of(1)));
        answers.add(new Answer<>(List.of(2)));
        answers.add(new Answer<>(List.of(3)));
        answers.add(new Answer<>(List.of(4)));
        answers.add(new Answer<>(List.of(5)));
        answers.add(new Answer<>(List.of(-1)));
    }

    @Test
    public void testQuestionSingleChoice() {
        QuestionSingleChoice q = new QuestionSingleChoice("q", 0, List.of("a","b","c","d"));
        assertEquals(1, q.check(answers.get(0)));
        for(int i = 1; i <= 6; i++)
            assertEquals(0, q.check(answers.get(i)));
    }

}
