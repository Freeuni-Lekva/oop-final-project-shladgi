package objects.questions;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class QuestionFillInBlanksTest {

    List<Integer> blanks;
    List<List<String>> corrects;

    @BeforeEach
    void setUp() {
        blanks = new ArrayList<>();
        corrects = new ArrayList<>();
    }

    @Test
    void  questionFillInBlanksTestCheck1() {
        String  question = "i _ a person";
        blanks.add(2);
        corrects.add(new ArrayList<String>());
        corrects.get(0).add("am");
        corrects.get(0).add("was");

        QuestionFillInBlanks q = new QuestionFillInBlanks(question, blanks, corrects, false);
        List<String> ls = new ArrayList<>();
        // there should not be null, it throws an error.
        ls.add("am");
        Answer<String> ans = new Answer<>(ls);

        assertEquals(1, q.check(ans));

        ls.clear();
        ls.add("A M");
        ans = new Answer<>(ls);
        assertEquals(1, q.check(ans));
    }

    @Test
    void  questionFillInBlanksTestCheck2() {
        String  question = "i _ a _ person";
        blanks.add(2);
        blanks.add(6);
        for(int i = 0; i < blanks.size(); i++){
            corrects.add(new ArrayList<>());
        }
        corrects.get(0).add("am");
        corrects.get(0).add("was");

        corrects.get(1).add("good");

        QuestionFillInBlanks q = new QuestionFillInBlanks(question, blanks, corrects, false);
        List<String> ls = new ArrayList<>();
        ls.add("am");
        ls.add("");

        Answer<String> ans = new Answer<>(ls);
        assertEquals(1, q.check(ans));

        ls.clear();
        ls.add("w   a    S");
        ls.add("gooD");
        ans = new Answer<>(ls);
        assertEquals(2, q.check(ans));
    }

    @Test
    void  questionFillInBlanksTestCheck3() {
        String  question = "i _ a _ person and i have many _";
        blanks.add(2);
        blanks.add(6);
        blanks.add(31);

        for(int i = 0; i < blanks.size(); i++){
            corrects.add(new ArrayList<>());
        }

        corrects.get(0).add("am");
        corrects.get(0).add("was");

        corrects.get(1).add("good");
        corrects.get(1).add("bad");

        corrects.get(2).add("dogs");
        corrects.get(2).add("bitches");
        corrects.get(2).add("Test to write");

        QuestionFillInBlanks q = new QuestionFillInBlanks(question, blanks, corrects, true);
        List<String> ls = new ArrayList<>();
        ls.add("am");
        ls.add("Ba d");
        ls.add("test to write");

        Answer<String> ans = new Answer<>(ls);
        assertEquals(1, q.check(ans));

        ls.clear();
        ls.add("was");
        ls.add("bad");
        ls.add("Test to write");
        ans = new Answer<>(ls);

        assertEquals(3, q.check(ans));
    }

    @Test
    void  questionFillInBlanksTest_getDataAndPutData1() {
        String question = "i _ a _ student";
        // blanks
        List<Integer> blanks = new ArrayList<>(List.of(2, 6));

        // correctAnswers
        List<List<String>> corrects = new ArrayList<>();
        List<String> blank1 = new ArrayList<>(List.of("am", "was"));
        List<String> blank2 = new ArrayList<>(List.of("good", "bad"));
        corrects.add(blank1);
        corrects.add(blank2);

        // exactMatch
        boolean exactMatch = false;

        // create a QuestionFillInBlanks type question
        QuestionFillInBlanks q = new QuestionFillInBlanks(question, blanks, corrects, exactMatch);
        // store data from q
        JsonObject data = q.getData();

        // QuestionFillInBlanks(int id, int quizId, String question, String imageLink, int maxScore, JsonObject json)
        // simulate to create a quetsion from database, by using a second constructor.
        // this constructor make putData by itself, using data i gave it.
        QuestionFillInBlanks loaded = new QuestionFillInBlanks(700, 42, question, null, 2, 5.0, data);

        assertEquals(q.getQuestion(), loaded.getQuestion());
        assertEquals(q.getMaxScore(), loaded.getMaxScore());

/*
        assertEquals(q.blankIdx, loaded.blankIdx);
        assertEquals(q.correctAnswers, loaded.correctAnswers);
        assertEquals(q.exactMatch, loaded.exactMatch);
*/
        List<String> ls = new ArrayList<>();
        ls.add("am");
        ls.add("Ba d");
        Answer<String> ans = new Answer<>(ls);
        assertEquals(2, q.check(ans));
        assertEquals(2, loaded.check(ans));

        ls.clear();
        ls.add("am");
        ls.add("smart");
        assertEquals(1, q.check(ans));
        assertEquals(1, loaded.check(ans));

    }

    @Test
    void questionFillInBlanksTest_getDataAndPutData_exactMatchTrue() {
        String question = "I _ to _ the _ because it _";
        // blank positions
        List<Integer> blanks = new ArrayList<>(List.of(2, 7, 16, 33));

        // correctAnswers
        List<List<String>> corrects = new ArrayList<>();
        corrects.add(new ArrayList<>(List.of("went", "go")));
        corrects.add(new ArrayList<>(List.of("see", "visit")));
        corrects.add(new ArrayList<>(List.of("museum")));
        corrects.add(new ArrayList<>(List.of("closed", "was closed")));

        boolean exactMatch = true;

        QuestionFillInBlanks q = new QuestionFillInBlanks(question, blanks, corrects, exactMatch);
        JsonObject data = q.getData();

        QuestionFillInBlanks loaded = new QuestionFillInBlanks(1123, 456, question, null, 4, 1.0, data);

        assertEquals(q.getQuestion(), loaded.getQuestion());
        assertEquals(q.getMaxScore(), loaded.getMaxScore());

        // all answers match exactly
        List<String> answers1 = new ArrayList<>(List.of("went", "see", "museum", "closed"));
        Answer<String> ans1 = new Answer<>(answers1);
        assertEquals(4, q.check(ans1));
        assertEquals(4, loaded.check(ans1));

        // one answer is almost right but not exactly
        List<String> answers2 = new ArrayList<>(List.of("Went", "see", "museum", "closed")); // capitalized first
        Answer<String> ans2 = new Answer<>(answers2);
        assertEquals(3, q.check(ans2));
        assertEquals(3, loaded.check(ans2));

        // Negative test: wrong last 2 answers
        List<String> answers3 = new ArrayList<>(List.of("went", "visit", "m useum", "close"));
        Answer<String> ans3 = new Answer<>(answers3);
        assertEquals(2, q.check(ans3));
        assertEquals(2, loaded.check(ans3));
    }





}
