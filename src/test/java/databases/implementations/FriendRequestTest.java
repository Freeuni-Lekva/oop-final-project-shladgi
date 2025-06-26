package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendRequestField;
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
public class FriendRequestTest {
    private static Connection conn;
    private static FriendRequestDB userAchDB;
    private static List<FilterCondition<FriendRequestField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE friend_requests\n" +
                "(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    firstid      INT       NOT NULL,\n" +
                "    secondid     INT       NOT NULL,\n" +
                "    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                "    -- FOREIGN KEY (firstid) REFERENCES users (id),\n" +
                "    -- FOREIGN KEY (secondid) REFERENCES users (id)\n" +
                ");");
        userAchDB = new FriendRequestDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<FriendRequestField>> filters, FriendRequestDB db) {
        List<FriendRequest> friendRequests = db.query(filters);
        assertEquals(affected, friendRequests.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(FriendRequest u : friendRequests) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }

    @Test
    @Order(1)
    public void testAdding(){
        // Create FriendRequest objects
        FriendRequest fr1 = new FriendRequest(101, 201, LocalDateTime.of(2024, 1, 1, 10 ,0));
        FriendRequest fr2 = new FriendRequest(102, 202, LocalDateTime.of(2024, 1, 5, 11, 30));
        FriendRequest fr3 = new FriendRequest(101, 203, LocalDateTime.of(2024, 1, 10, 12, 0));
        FriendRequest fr4 = new FriendRequest(103, 201, LocalDateTime.of(2024, 1, 15, 13, 15));
        FriendRequest fr5 = new FriendRequest(104, 101, LocalDateTime.of(2024, 1, 20, 14, 45));
        FriendRequest fr6 = new FriendRequest(105, 205, LocalDateTime.of(2024, 2, 1, 9, 0));

        assertEquals(0, userAchDB.query(allFilter).size());
        userAchDB.add(fr1);
        assertEquals(1, userAchDB.query(allFilter).size());
        userAchDB.add(fr2);
        assertEquals(2, userAchDB.query(allFilter).size());
        userAchDB.add(fr3);
        assertEquals(3, userAchDB.query(allFilter).size());
        userAchDB.add(fr4);
        assertEquals(4, userAchDB.query(allFilter).size());
        userAchDB.add(fr5);
        assertEquals(5, userAchDB.query(allFilter).size());
        userAchDB.add(fr6);
        assertEquals(6, userAchDB.query(allFilter).size());
    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<FriendRequest> results = userAchDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<FriendRequestField>> filters = List.of(
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, 101)
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);


        filters = List.of(
                new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, 201)
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);


        filters = List.of(
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, 101),
                new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, 203)
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(FriendRequestField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2024, 1, 10, 0, 0).toString())
        );
        testQueryDeleteAddOnce(size, 4, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(FriendRequestField.CREATIONDATE, Operator.LESSEQ, LocalDateTime.of(2024, 1, 5, 11, 30).toString())
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, 101),
                new FilterCondition<>(FriendRequestField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2024, 1, 5, 0, 0).toString())
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);


        filters = List.of(
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, 999),
                new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, 888)
        );
        testQueryDeleteAddOnce(size, 0, filters, userAchDB);

        testQueryDeleteAddOnce(size, size, allFilter, userAchDB);
    }
}
