package tests.objects.questions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import objects.questions.Answer;
import objects.questions.QuestionMultiChoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class QuestionMultiChoiceTest {
    private QuestionMultiChoice q;
    private List<String> choices = Arrays.asList("A", "B", "C", "D");

    @Test
    public void testCheck1() {
        q = new QuestionMultiChoice("?",Arrays.asList(1) ,choices,false);
        assertEquals(1,q.check(new Answer<>(Arrays.asList(1))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList())));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(1,2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,1,2))));
    }

    @Test
    public void testCheck2() {
        q = new QuestionMultiChoice("?",Arrays.asList(1,2) ,choices,false);
        assertEquals(1,q.check(new Answer<>(Arrays.asList(1))));
        assertEquals(1,q.check(new Answer<>(Arrays.asList(2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList())));
        assertEquals(2,q.check(new Answer<>(Arrays.asList(1,2))));
        assertEquals(1,q.check(new Answer<>(Arrays.asList(0,1,2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,1,2,3))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,3))));
    }

    @Test
    public void testCheckWithExaxtMatch() {
        q = new QuestionMultiChoice("?",Arrays.asList(1,2) ,choices,true);
        assertEquals(0,q.check(new Answer<>(Arrays.asList(1))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList())));
        assertEquals(2,q.check(new Answer<>(Arrays.asList(1,2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,1,2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,1,2,3))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,3))));
    }

    @Test
    public void testGetPutData(){
        q = new QuestionMultiChoice("?",Arrays.asList(1,2) ,choices,false);
        JsonObject jo = q.getData();
        assertFalse(jo.get(EM).getAsBoolean());
        assertEquals(4,jo.getAsJsonArray(CH).size());
        assertEquals(2,jo.getAsJsonArray(CC).size());

        QuestionMultiChoice q2 = new QuestionMultiChoice(1,1,"?","link",jo.getAsJsonArray(CC).size(),jo);

        assertEquals(1,q2.check(new Answer<>(Arrays.asList(1))));
        assertEquals(1,q2.check(new Answer<>(Arrays.asList(2))));
        assertEquals(2,q2.check(new Answer<>(Arrays.asList(1,2))));
        assertEquals(1,q2.check(new Answer<>(Arrays.asList(1,2,3))));

    }

    @Test
    public void testPutData(){
        JsonObject jo = new JsonObject();

        JsonArray jaChoices = new JsonArray();
        for(String c: choices )jaChoices.add(c);
        jo.add(CH,jaChoices);
        JsonArray jaCorrectChoises = new JsonArray();
        jaCorrectChoises.add(1);
        jaCorrectChoises.add(2);
        jaCorrectChoises.add(3);
        jo.add(CC, jaCorrectChoises);
        jo.addProperty(EM, true);
        q =new QuestionMultiChoice(1,1,"?","link",3,jo);
        assertEquals(3,q.check(new Answer<>(Arrays.asList(1,2,3))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(1,2))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(0,3))));
        assertEquals(0,q.check(new Answer<>(Arrays.asList())));
        assertEquals(0,q.check(new Answer<>(Arrays.asList(1,2,3,0))));
    }

    private static String EM = "exactMatch";
    private static String CH = "choices";
    private static String CC = "correctChoices";

}
