package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
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
public class QuizResultTest {
    private static Connection conn;
    private static QuizResultDB quizResultDB;
    private static List<FilterCondition<QuizResultField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE quiz_results\n" +
                "(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    userid       INT            NOT NULL,\n" +
                "    quizid       INT            NOT NULL,\n" +
                "    creationdate TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    timetaken    INT            NOT NULL DEFAULT 0,  -- seconds\n" +
                "    totalscore   DECIMAL(10, 2) NOT NULL DEFAULT 0\n" +
                "  --  FOREIGN KEY (userid) REFERENCES users (id),\n" +
                "  --  FOREIGN KEY (quizid) REFERENCES quizzes (id)\n" +
                ");");
        quizResultDB = new QuizResultDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<QuizResultField>> filters, QuizResultDB db) {
        List<QuizResult> quizResults = db.query(filters);
        assertEquals(affected, quizResults.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(QuizResult u : quizResults) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }
    
    @Test
    @Order(1)
    public void testAdding() {
        QuizResult r1 = new QuizResult(1, 4, LocalDateTime.of(2020, 1, 1, 10, 0), 10.0, 100);
        QuizResult r2 = new QuizResult(2, 5, LocalDateTime.of(2020, 2, 1, 11, 0), 15.5, 500);
        QuizResult r3 = new QuizResult(3, 6, LocalDateTime.of(2020, 3, 1, 12, 0), 20.0, 200);
        QuizResult r4 = new QuizResult(4, 7, LocalDateTime.of(2020, 4, 1, 13, 0), 25.5, 0);
        QuizResult r5 = new QuizResult(5, 8, LocalDateTime.of(2020, 5, 1, 14, 0), 30.0, 50);
        QuizResult r6 = new QuizResult(6, 9, LocalDateTime.of(2020, 6, 1, 15, 0), 35.5, 100);

        quizResultDB.add(r1);
        assertEquals(1, quizResultDB.query(allFilter).size());

        quizResultDB.add(r2);
        assertEquals(2, quizResultDB.query(allFilter).size());
        quizResultDB.add(r3);
        assertEquals(3, quizResultDB.query(allFilter).size());
        quizResultDB.add(r4);
        assertEquals(4, quizResultDB.query(allFilter).size());
        quizResultDB.add(r5);
        assertEquals(5, quizResultDB.query(allFilter).size());
        quizResultDB.add(r6);
        assertEquals(6, quizResultDB.query(allFilter).size());

        assertEquals(1, quizResultDB.query("WHERE userid = 2").size());
        assertEquals(4, quizResultDB.query("WHERE quizid <= 5 OR quizid >= 8").size());
    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<QuizResult> results = quizResultDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<QuizResultField>> filters = List.of(
                new FilterCondition<>(QuizResultField.USERID, Operator.LESSEQ, 2)
        );
        testQueryDeleteAddOnce(size, 2, filters, quizResultDB);

        filters = List.of(
                new FilterCondition<>(QuizResultField.USERID, Operator.MORE, 2),
                new FilterCondition<>(QuizResultField.QUIZID, Operator.LESS, 8)
        );
        testQueryDeleteAddOnce(size, 2, filters, quizResultDB);

        filters = List.of(
                new FilterCondition<>(QuizResultField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2020, 3, 1, 0, 0).toString()),
                new FilterCondition<>(QuizResultField.TOTALSCORE, Operator.LESSEQ, 25.5)
        );
        testQueryDeleteAddOnce(size, 2, filters, quizResultDB);

        filters = List.of(
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.LESSEQ, 200),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MOREEQ, 100)
        );
        testQueryDeleteAddOnce(size, 3, filters, quizResultDB);

        testQueryDeleteAddOnce(size, size, allFilter, quizResultDB);
    }
}
