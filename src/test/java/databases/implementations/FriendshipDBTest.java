package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendshipField;
import objects.user.Friendship;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FriendshipDBTest {
    private static Connection conn;
    private static FriendshipDB friendshipDB;
    private static List<FilterCondition<FriendshipField>> allFilter;


    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE friendships\n" +
                "(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    firstid      INT       NOT NULL,\n" +
                "    secondid     INT       NOT NULL,\n" +
                "    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " --   FOREIGN KEY (firstid) REFERENCES users (id),\n" +
                " --   FOREIGN KEY (secondid) REFERENCES users (id),\n" +
                "    \n" +
                "    -- Ensure no duplicate friendships (regardless of order)\n" +
                "    CONSTRAINT unique_friendship UNIQUE (firstid, secondid),\n" +
                "    CONSTRAINT check_ids CHECK (firstid < secondid)\n" +
                ");");
        friendshipDB = new FriendshipDB(conn);
    }
    
    @Test
    @Order(1)
    public void testAdding() {
        Friendship f1 = new Friendship(1, 2, LocalDateTime.of(2020, 2, 1, 10, 0));
        Friendship f2 = new Friendship(1, 3, LocalDateTime.of(2011, 1, 1, 11, 0));
        Friendship f3 = new Friendship(2, 3, LocalDateTime.of(2019, 1, 1, 12, 0));
        Friendship f4 = new Friendship(1, 4, LocalDateTime.of(2020, 2, 1, 13, 0));
        Friendship f5 = new Friendship(2, 4, LocalDateTime.of(2020, 2, 1, 14, 0));
        Friendship f6 = new Friendship(3, 4, LocalDateTime.of(2020, 2, 1, 15, 0));

        assertEquals(0, friendshipDB.query(allFilter).size());
        friendshipDB.add(f1);
        assertEquals(1, friendshipDB.query(allFilter).size());
        friendshipDB.add(f2);
        assertEquals(2, friendshipDB.query(allFilter).size());
        friendshipDB.add(f3);
        assertEquals(3, friendshipDB.query(allFilter).size());
        friendshipDB.add(f4);
        assertEquals(4, friendshipDB.query(allFilter).size());
        friendshipDB.add(f5);
        assertEquals(5, friendshipDB.query(allFilter).size());
        friendshipDB.add(f6);
        assertEquals(6, friendshipDB.query(allFilter).size());
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<FriendshipField>> filters, FriendshipDB db) {
        List<Friendship> fships = db.query(filters);
        assertEquals(affected, fships.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(Friendship f : fships) db.add(f);
        assertEquals(startSize, db.query(allFilter).size());
    }


    @Test
    @Order(2)
    public void testQueryDeleteAdd(){
        List<Friendship> friends = friendshipDB.query(allFilter);
        int size = friends.size();
        assertEquals(6, size);

        List<FilterCondition<FriendshipField>> filters = List.of(
                new FilterCondition<>(FriendshipField.FIRSTID, Operator.LESSEQ, 2)
        );
        testQueryDeleteAddOnce(size, 5, filters, friendshipDB);

        filters = List.of(
                new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, 2)
        );
        testQueryDeleteAddOnce(size, 2, filters, friendshipDB);

        filters = List.of(
                new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, 2)
        );
        testQueryDeleteAddOnce(size, 1, filters, friendshipDB);

        filters = List.of(
                new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, 1),
                new FilterCondition<>(FriendshipField.SECONDID, Operator.MORE, 2)
        );
        testQueryDeleteAddOnce(size, 2, filters, friendshipDB);

        filters = List.of(
                new FilterCondition<>(FriendshipField.CREATIONDATE, Operator.LESS, LocalDateTime.of(2020, 1, 1, 1, 1).toString())
        );
        testQueryDeleteAddOnce(size, 2, filters, friendshipDB);

        filters = List.of(
                new FilterCondition<>(FriendshipField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2020, 1, 1, 1, 1).toString()),
                new FilterCondition<>(FriendshipField.FIRSTID, Operator.MOREEQ, 2)
        );
        testQueryDeleteAddOnce(size, 2, filters, friendshipDB);

        testQueryDeleteAddOnce(size, size, allFilter, friendshipDB);
    }
}
