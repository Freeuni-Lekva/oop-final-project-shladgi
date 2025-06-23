package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.fields.FriendRequestField;
import objects.user.FriendRequest;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                "    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    FOREIGN KEY (firstid) REFERENCES users (id),\n" +
                "    FOREIGN KEY (secondid) REFERENCES users (id)\n" +
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
}
