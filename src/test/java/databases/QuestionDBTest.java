package databases;

import databases.filters.FilterQuestion;
import objects.questions.Question;
import objects.questions.QuestionSingleChoice;
import org.junit.Before;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionDBTest {

    private static Connection conn;
    private static QuestionDB questionDB;

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE questions\n" +
                "(\n" +
                "    questionid INT PRIMARY KEY AUTO_INCREMENT,\n" +
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
        QuestionSingleChoice q2 = new QuestionSingleChoice("Q2", 0, List.of("ans3", "ans4"));
        QuestionSingleChoice q3 = new QuestionSingleChoice("Q3", 3, List.of("ans5", "ans6", "ans7", "ans8"));

        FilterQuestion allFilter = FilterQuestion.all();
        assertEquals(0, questionDB.query(allFilter).size());
        questionDB.add(q);
        assertEquals(1, questionDB.query(allFilter).size());
        questionDB.add(q2);
        assertEquals(2, questionDB.query(allFilter).size());
        questionDB.add(q3);
        assertEquals(3, questionDB.query(allFilter).size());
    }

}
