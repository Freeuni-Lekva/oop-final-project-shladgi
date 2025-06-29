package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.ChallengeField;
import databases.filters.fields.FriendRequestField;
import objects.user.Challenge;
import objects.user.FriendRequest;
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
public class ChallengeDBTest {
    private static Connection conn;
    private static ChallengeDB challengeDB;
    private static List<FilterCondition<ChallengeField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE challenges\n" +
                "(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    quizid       INT            NOT NULL,\n" +
                "    senderid     INT            NOT NULL,\n" +
                "    recipientid  INT            NOT NULL,\n" +
                "    bestscore    DECIMAL(10, 2) NOT NULL DEFAULT 0,\n" +
                "    quiztitle    VARCHAR(255)   NOT NULL,\n" +
                "    creationdate TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                "    -- FOREIGN KEY (quizid) REFERENCES quizzes (id),\n" +
                "    -- FOREIGN KEY (senderid) REFERENCES users (id),\n" +
                "    -- FOREIGN KEY (recipientid) REFERENCES users (id)\n" +
                ");");
        challengeDB = new ChallengeDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<ChallengeField>> filters, ChallengeDB db) {
        List<Challenge> challenges = db.query(filters);
        assertEquals(affected, challenges.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(Challenge u : challenges) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }

    @Test
    @Order(1)
    public void testAdding(){
        Challenge c1 = new Challenge(1, 1, 2, 10.0, "Quiz 1", LocalDateTime.of(2024, 1, 1, 10, 0));
        Challenge c2 = new Challenge(2, 1, 3, 15.5, "Quiz 2", LocalDateTime.of(2024, 1, 5, 11, 30));
        Challenge c3 = new Challenge(2, 3, 4, 20.0, "Quiz 3", LocalDateTime.of(2024, 1, 10, 12, 0));    
        Challenge c4 = new Challenge(3, 3, 4, 25.5, "Quiz 4", LocalDateTime.of(2024, 1, 15, 13, 15));    
        Challenge c5 = new Challenge(3, 5, 6, 30.0, "Quiz 5", LocalDateTime.of(2024, 1, 20, 14, 45));    
        Challenge c6 = new Challenge(6, 6, 7, 35.5, "Quiz 6", LocalDateTime.of(2024, 2, 1, 9, 0));

        challengeDB.add(c1);
        assertEquals(1, challengeDB.query(allFilter).size());
        challengeDB.add(c2);
        assertEquals(2, challengeDB.query(allFilter).size());
        challengeDB.add(c3);
        assertEquals(3, challengeDB.query(allFilter).size());
        challengeDB.add(c4);
        assertEquals(4, challengeDB.query(allFilter).size());
        challengeDB.add(c5);
        assertEquals(5, challengeDB.query(allFilter).size());
        challengeDB.add(c6);
        assertEquals(6, challengeDB.query(allFilter).size());

    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<Challenge> results = challengeDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<ChallengeField>> filters = List.of(
                new FilterCondition<>(ChallengeField.SENDERID, Operator.EQUALS, 2)
        );
        testQueryDeleteAddOnce(size, 0, filters, challengeDB);
        
        filters = List.of(
                new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, 3)
        );
        testQueryDeleteAddOnce(size, 1, filters, challengeDB);
        
        filters = List.of(
                new FilterCondition<>(ChallengeField.QUIZID, Operator.MOREEQ, 3)
        );
        testQueryDeleteAddOnce(size, 3, filters, challengeDB);

        filters = List.of(
                new FilterCondition<>(ChallengeField.SENDERID, Operator.EQUALS, 3),
                new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, 4)
        );
        testQueryDeleteAddOnce(size, 2, filters, challengeDB);

        filters = List.of(
                new FilterCondition<>(ChallengeField.QUIZID, Operator.EQUALS, 3)
        );
        testQueryDeleteAddOnce(size, 2, filters, challengeDB);

        filters = List.of(
                new FilterCondition<>(ChallengeField.QUIZID, Operator.MOREEQ, 2),
                new FilterCondition<>(ChallengeField.SENDERID, Operator.LESSEQ, 3)
        );
        testQueryDeleteAddOnce(size, 3, filters, challengeDB);

        testQueryDeleteAddOnce(size, size, allFilter, challengeDB);
    }
}
