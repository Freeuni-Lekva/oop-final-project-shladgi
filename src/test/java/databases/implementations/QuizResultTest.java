package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserAchievementField;
import objects.user.QuizResult;
import objects.user.UserAchievement;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuizResultTest {
    private static Connection conn;
    private static QuizResultDB userAchDB;
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
                "    totalscore   DECIMAL(10, 2) NOT NULL DEFAULT 0,\n" +
                "    -- FOREIGN KEY (userid) REFERENCES users (id),\n" +
                "    -- FOREIGN KEY (quizid) REFERENCES quizzes (id)\n" +
                ");");
        userAchDB = new QuizResultDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<QuizResultField>> filters, QuizResultDB db) {
        List<QuizResult> quizResults = db.query(filters);
        assertEquals(affected, quizResults.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(QuizResult u : quizResults) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }
}
