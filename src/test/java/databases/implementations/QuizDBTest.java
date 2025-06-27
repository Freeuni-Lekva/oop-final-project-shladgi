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
    private static QuizDB userAchDB;
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
                "    timelimit           INT            NOT NULL DEFAULT -1 -- IN SECONDS\n" +
                "  --  FOREIGN KEY (userid) REFERENCES users (id)\n" +
                ");");
        userAchDB = new QuizDB(conn);
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
                100.0, 10, false, false, true, false);
        Quiz q2 = new Quiz("quiz2", 2, LocalDateTime.of(2021, 3, 15, 14, 30), 100,
                90.5, 15, true, true, false, true);
        Quiz q3 = new Quiz("quiz3", 3, LocalDateTime.of(2022, 6, 20, 9, 15), 200,
                75.0, 8, false, true, true, false);
        Quiz q4 = new Quiz("quiz4", 1, LocalDateTime.of(2023, 9, 10, 16, 45), 10,
                95.5, 12, true, false, false, true);
        Quiz q5 = new Quiz("quiz5", 2, LocalDateTime.of(2024, 12, 5, 11, 20), 30,
                85.0, 20, false, true, true, false);
        Quiz q6 = new Quiz("quiz6", 3, LocalDateTime.of(2025, 4, 25, 13, 40), -1,
                88.5, 18, true, false, true, true);

        assertEquals(0, userAchDB.query(allFilter).size());
        userAchDB.add(q1);
        assertEquals(1, userAchDB.query(allFilter).size());
        userAchDB.add(q2);
        assertEquals(2, userAchDB.query(allFilter).size());
        userAchDB.add(q3);
        assertEquals(3, userAchDB.query(allFilter).size());
        userAchDB.add(q4);
        assertEquals(4, userAchDB.query(allFilter).size());
        userAchDB.add(q5);
        assertEquals(5, userAchDB.query(allFilter).size());
        userAchDB.add(q6);
        assertEquals(6, userAchDB.query(allFilter).size());
    }
    
    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<Quiz> results = userAchDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<QuizField>> filters = List.of(
         new FilterCondition<>(QuizField.USERID, Operator.EQUALS, 1)
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);


        filters = List.of(
                new FilterCondition<>(QuizField.USERID, Operator.LESSEQ, 2),
                new FilterCondition<>(QuizField.TITLE, Operator.EQUALS, "quiz1")
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(QuizField.USERID, Operator.MORE, 2),
                new FilterCondition<>(QuizField.TITLE, Operator.EQUALS, "quiz1")
        );
        testQueryDeleteAddOnce(size, 0, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(QuizField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2020, 3, 1, 0, 0).toString()),
                new FilterCondition<>(QuizField.TOTALSCORE, Operator.MOREEQ, 95.5)
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(QuizField.RANDOM, Operator.EQUALS, true),
                new FilterCondition<>(QuizField.SINGLEPAGE, Operator.EQUALS, false)
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(QuizField.RANDOM, Operator.EQUALS, true),
                new FilterCondition<>(QuizField.SINGLEPAGE, Operator.EQUALS, false),
                new FilterCondition<>(QuizField.TOTALQUESTIONS, Operator.MORE, 14)
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);


        testQueryDeleteAddOnce(size, size, allFilter, userAchDB);

    }
        

}
