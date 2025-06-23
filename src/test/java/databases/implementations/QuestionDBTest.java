package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuestionField;
import objects.questions.Question;
import objects.questions.QuestionSingleChoice;
import objects.questions.QuestionTextAnswer;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuestionDBTest {

    private static Connection conn;
    private static QuestionDB questionDB;
    private static List<FilterCondition<QuestionField>> allFilter;


    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE questions\n" +
                "(\n" +
                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    quizid     INT                                                                                                  NOT NULL,\n" +
                "    question   VARCHAR(1000)                                                                                        NOT NULL,\n" +
                "    imagelink  VARCHAR(255),\n" +
                "    type       ENUM ('SingleChoice', 'MultiChoice', 'TextAnswer', 'MultiTextAnswer', 'FillInBlanks', 'FillChoices') NOT NULL,\n" +
                "    maxscore   INT                                                                                                  NOT NULL,\n" +
                "    jsondata   VARCHAR(65535)\n" + // had to set it to VARCHAR instead of JSON (JSON type works differently in h2) but this works the same
                ");");
        questionDB = new QuestionDB(conn);
    }

    @Test
    @Order(1)
    void testAdding() {
        QuestionSingleChoice q = new QuestionSingleChoice("Q1", 1, List.of("ans1", "ans2"));
        q.setQuizId(1);
        QuestionSingleChoice q2 = new QuestionSingleChoice("Q2", 0, List.of("ans3", "ans4"));
        q2.setQuizId(2);
        QuestionSingleChoice q3 = new QuestionSingleChoice("Q3", 3, List.of("ans5", "ans6", "ans7", "ans8"));
        q3.setQuizId(3);
        QuestionTextAnswer q4 = new QuestionTextAnswer("Q", List.of("ans1"), true);
        q4.setQuizId(4);
        QuestionTextAnswer q5 = new QuestionTextAnswer("Q", List.of("ans2"), false);
        q5.setQuizId(4);


        assertEquals(0, questionDB.query(allFilter).size());
        questionDB.add(q);
        assertEquals(1, questionDB.query(allFilter).size());
        questionDB.add(q2);
        assertEquals(2, questionDB.query(allFilter).size());
        questionDB.add(q3);
        assertEquals(3, questionDB.query(allFilter).size());
        questionDB.add(q4);
        assertEquals(4, questionDB.query(allFilter).size());
        questionDB.add(q5);
        assertEquals(5, questionDB.query(allFilter).size());
    }

    @Test
    @Order(2)
    void testDeleting() {
        List<Question> questions = questionDB.query(allFilter);
        int size = questions.size();

        // delete by small amounts
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 1)));
        assertEquals( size - 1, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 2)));
        assertEquals(size - 2, questionDB.query(allFilter).size());
        assertEquals(0, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 1)));
        assertEquals(size - 2, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(List.of(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 3))));
        assertEquals(size - 3, questionDB.query(allFilter).size());
        assertEquals(2, questionDB.delete(List.of(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 4))));
        assertEquals(size - 5, questionDB.query(allFilter).size());

        questionDB.delete(allFilter);
        assertEquals(0, questionDB.query(allFilter).size());

        // add back and delete everything
        for(Question q : questions) questionDB.add(q);
        assertEquals(size, questionDB.query(allFilter).size());
        assertEquals(size, questionDB.delete(allFilter));
        assertEquals(0, questionDB.query(allFilter).size());

    }
    
    @Test
    @Order(3)
    void testAddAndDelete(){
        QuestionSingleChoice q1 = new QuestionSingleChoice("Q1", 1, List.of("ans1", "ans2"));
        q1.setQuizId(1);
        QuestionSingleChoice q2 = new QuestionSingleChoice("Q2", 0, List.of("ans3", "ans4"));
        q2.setQuizId(2);
        QuestionSingleChoice q3 = new QuestionSingleChoice("Q3", 3, List.of("ans5", "ans6", "ans7", "ans8"));
        q3.setQuizId(3);
        QuestionTextAnswer q4 = new QuestionTextAnswer("Q", List.of("ans1"), true);
        q4.setQuizId(4);
        QuestionTextAnswer q5 = new QuestionTextAnswer("Q", List.of("ans2"), false);
        q5.setQuizId(5);

        // Test adding one by one and deleting
        assertEquals(0, questionDB.query(allFilter).size());
        questionDB.add(q1);
        assertEquals(1, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 1)));
        assertEquals(0, questionDB.query(allFilter).size());

        questionDB.add(q2);
        assertEquals(1, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 2)));
        assertEquals(0, questionDB.query(allFilter).size());

        // Test adding multiple and deleting one by one
        questionDB.add(q3);
        questionDB.add(q1);
        questionDB.add(q5);
        questionDB.add(q2);
        questionDB.add(q4);
        assertEquals(5, questionDB.query(allFilter).size());

        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 3)));
        assertEquals(4, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 5)));
        assertEquals(3, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 2)));
        assertEquals(2, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 4)));
        assertEquals(1, questionDB.query(allFilter).size());
        assertEquals(1, questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, 1)));
        assertEquals(0, questionDB.query(allFilter).size());
    }


}
