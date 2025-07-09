package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import objects.Quiz;
import objects.user.QuizResult;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizDBTest {
    private static Connection conn;
    private static QuizDB quizDB;
    private static List<FilterCondition<QuizField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE quizzes\n" +
                "(\n" +
                "    id              INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    title               VARCHAR(255)   NOT NULL,\n" +
                "    userid              INT            NOT NULL,\n" +
                "    creationdate        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    totalscore          DECIMAL(10, 2) NOT NULL DEFAULT 0,\n" +
                "    totalquestions      INT            NOT NULL DEFAULT 0,\n" +
                "    random            BOOLEAN        NOT NULL DEFAULT FALSE,\n" +
                "    singlepage          BOOLEAN        NOT NULL DEFAULT TRUE,\n" +
                "    immediatecorrection BOOLEAN        NOT NULL DEFAULT FALSE,\n" +
                "    practicemode        BOOLEAN        NOT NULL DEFAULT TRUE,\n" +
                "    timelimit           INT            NOT NULL DEFAULT -1, -- IN SECONDS\n" +
                "    description         VARCHAR(1000)   NOT NULL\n" +
                "  --  FOREIGN KEY (userid) REFERENCES users (id)\n" +
                ");");
        quizDB = new QuizDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<QuizField>> filters, QuizDB db) {
        List<Quiz> quizzes = db.query(filters);
        assertEquals(affected, quizzes.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(Quiz u : quizzes) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }
    
    @Test
    @Order(1)
    public void testAdding(){

        Quiz q1 = new Quiz("quiz1", 1, LocalDateTime.of(2020, 1, 1, 10 ,0), -1,
                100.0, 10, false, false, true, false, "small descrription");
        Quiz q2 = new Quiz("quiz2", 2, LocalDateTime.of(2021, 3, 15, 14, 30), 100,
                90.5, 15, true, true, false, true, "biiiiig descrription");
        Quiz q3 = new Quiz("quiz3", 3, LocalDateTime.of(2022, 6, 20, 9, 15), 200,
                75.0, 8, false, true, true, false, "very biiiiiiiig descrription");
        Quiz q4 = new Quiz("quiz4", 1, LocalDateTime.of(2023, 9, 10, 16, 45), 10,
                95.5, 12, true, false, false, true, "sml descrription");
        Quiz q5 = new Quiz("quiz5", 2, LocalDateTime.of(2024, 12, 5, 11, 20), 30,
                85.0, 20, false, true, true, false, "description");
        Quiz q6 = new Quiz("quiz6", 3, LocalDateTime.of(2025, 4, 25, 13, 40), -1,
                88.5, 18, true, false, true, true, "random description");

        assertEquals(0, quizDB.query(allFilter).size());
        quizDB.add(q1);
        assertEquals(1, quizDB.query(allFilter).size());
        quizDB.add(q2);
        assertEquals(2, quizDB.query(allFilter).size());
        quizDB.add(q3);
        assertEquals(3, quizDB.query(allFilter).size());
        quizDB.add(q4);
        assertEquals(4, quizDB.query(allFilter).size());
        quizDB.add(q5);
        assertEquals(5, quizDB.query(allFilter).size());
        quizDB.add(q6);
        assertEquals(6, quizDB.query(allFilter).size());

        List<Quiz> ls = quizDB.query(allFilter, QuizField.TIMELIMIT, true, 2, 2);
        assertEquals(2, ls.size());
        assertEquals(10, ls.get(0).getTimeLimit());
        assertEquals(30, ls.get(1).getTimeLimit());
    }
    
    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<Quiz> results = quizDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<QuizField>> filters = List.of(
         new FilterCondition<>(QuizField.USERID, Operator.EQUALS, 1)
        );
        testQueryDeleteAddOnce(size, 2, filters, quizDB);


        filters = List.of(
                new FilterCondition<>(QuizField.USERID, Operator.LESSEQ, 2),
                new FilterCondition<>(QuizField.TITLE, Operator.EQUALS, "quiz1")
        );
        testQueryDeleteAddOnce(size, 1, filters, quizDB);

        filters = List.of(
                new FilterCondition<>(QuizField.USERID, Operator.MORE, 2),
                new FilterCondition<>(QuizField.TITLE, Operator.EQUALS, "quiz1")
        );
        testQueryDeleteAddOnce(size, 0, filters, quizDB);

        filters = List.of(
                new FilterCondition<>(QuizField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2020, 3, 1, 0, 0).toString()),
                new FilterCondition<>(QuizField.TOTALSCORE, Operator.MOREEQ, 95.5)
        );
        testQueryDeleteAddOnce(size, 1, filters, quizDB);

        filters = List.of(
                new FilterCondition<>(QuizField.RANDOM, Operator.EQUALS, true),
                new FilterCondition<>(QuizField.SINGLEPAGE, Operator.EQUALS, false)
        );
        testQueryDeleteAddOnce(size, 2, filters, quizDB);

        filters = List.of(
                new FilterCondition<>(QuizField.RANDOM, Operator.EQUALS, true),
                new FilterCondition<>(QuizField.SINGLEPAGE, Operator.EQUALS, false),
                new FilterCondition<>(QuizField.TOTALQUESTIONS, Operator.MORE, 14)
        );
        testQueryDeleteAddOnce(size, 1, filters, quizDB);

        filters = List.of(
                new FilterCondition<>(QuizField.TIMELIMIT, Operator.MOREEQ, 50)
        );
        testQueryDeleteAddOnce(size, 2, filters, quizDB);

        filters = List.of(
            new FilterCondition<>(QuizField.TIMELIMIT, Operator.LESS, 50),
            new FilterCondition<>(QuizField.TIMELIMIT, Operator.MORE, 0),
            new FilterCondition<>(QuizField.PRACTICEMODE, Operator.EQUALS, true)
        );
        testQueryDeleteAddOnce(size, 1, filters, quizDB);

        testQueryDeleteAddOnce(size, size, allFilter, quizDB);

    }

}
