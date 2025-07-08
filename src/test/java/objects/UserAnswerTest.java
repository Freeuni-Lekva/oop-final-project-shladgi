package objects;

import com.google.gson.JsonObject;
import objects.questions.Answer;
import objects.user.UserAnswer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class UserAnswerTest {
    @Test
    public void userAnswerTest(){
        List<Integer> ls = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        Answer<Integer> ans = new Answer<Integer>(ls);
        UserAnswer userAnswer = new UserAnswer(4, 5, false, ans);
        assertEquals(4, userAnswer.getQuestionId());
        assertEquals(5, userAnswer.getResultId());
        assertEquals(false, userAnswer.isString());
        ans = userAnswer.getIntAnswer();
        int count = ans.getSize();
        assertEquals(10, count);
        for(int i = 0; i < count; i++)assertEquals(i+1, ans.get(i));
        JsonObject j = userAnswer.getData();

        UserAnswer userAnswer1 = new UserAnswer();
        userAnswer1.setQuestionId(4);
        userAnswer1.setResultId(5);
        userAnswer1.setIsString(false);
        userAnswer1.putData(j);
        assertEquals(4, userAnswer1.getQuestionId());
        assertEquals(5, userAnswer1.getResultId());
        assertEquals(false, userAnswer1.isString());
        Answer<Integer> ans1 = userAnswer1.getIntAnswer();
        int count1 = ans1.getSize();
        assertEquals(count , count1);
        for(int i = 0; i < count1; i++)assertEquals(i+1,  ans1.get(i));
    }

    @Test
    public void userAnswerTest2(){
        List<String> ls = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");
        Answer<String> ans = new Answer<String>(ls);
        UserAnswer userAnswer = new UserAnswer(4, 5, true, ans);
        assertEquals(4, userAnswer.getQuestionId());
        assertEquals(5, userAnswer.getResultId());
        assertEquals(true, userAnswer.isString());
        ans = userAnswer.getStrAnswer();
        int count = ans.getSize();
        assertEquals(10, count);
        for(int i = 0; i < count; i++)assertEquals(Integer.toString(i+1),  ans.get(i));
        JsonObject j = userAnswer.getData();

        UserAnswer userAnswer1 = new UserAnswer();
        userAnswer1.setQuestionId(4);
        userAnswer1.setResultId(5);
        userAnswer1.setIsString(true);
        userAnswer1.putData(j);
        assertEquals(4, userAnswer1.getQuestionId());
        assertEquals(5, userAnswer1.getResultId());
        assertEquals(true, userAnswer1.isString());
        Answer<String> ans1 = userAnswer1.getStrAnswer();
        int count1 = ans1.getSize();
        assertEquals(count , count1);
        for(int i = 0; i < count1; i++)assertEquals(Integer.toString(i+1),  ans.get(i));
    }
}
