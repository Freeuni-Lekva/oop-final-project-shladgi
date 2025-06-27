package objects.questions;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionSingleChoiceTest {

    private static List<Answer<Integer>> answers;


    // creates a question with 'total' amount of choices and the correctId is 'correct'
    // then checks it fully
    // then makes a new question with the second constructor and tests it again
    void checkCorrect(int correct, int total){

        // first part of correctness
        List<String> choices = new ArrayList<>();
        for(int i = 0; i < total; i++) choices.add("ans"+i);

        QuestionSingleChoice q = new QuestionSingleChoice("Q"+correct, correct, choices);

        assertEquals(1, q.check(new Answer<>(List.of(correct))));
        for(int i = -1; i < total+5; i++)
            if(i != correct)
                assertEquals(0, q.check(new Answer<>(List.of(i))));



        // second part of tests using a different  constructor
        QuestionSingleChoice newQ = new QuestionSingleChoice(1,2,"Q"+correct, "imgLink", 1, 2.0, q.getData());

        assertEquals(1, newQ.check(new Answer<>(List.of(correct))));
        for(int i = -1; i < total+5; i++)
            if(i != correct)
                assertEquals(0, newQ.check(new Answer<>(List.of(i))));

    }


    @Test
    public void testCorrectness() {
        // check a bunch of combinations with
        for(int i = 1; i <= 6; i++)
            for(int j = 0; j < i; j++)
                checkCorrect(i,j);

    }

}
