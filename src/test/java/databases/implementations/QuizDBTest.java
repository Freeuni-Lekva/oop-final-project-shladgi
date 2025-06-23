package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import objects.Quiz;
import objects.user.QuizResult;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                "    -- FOREIGN KEY (userid) REFERENCES users (id)\n" +
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
}
