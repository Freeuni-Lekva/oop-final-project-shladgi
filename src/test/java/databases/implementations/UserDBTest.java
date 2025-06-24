package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import objects.user.User;
import objects.user.UserType;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // this is so the test cases are run in order
public class UserDBTest {
    private static Connection conn;
    private static UserDB userDB;
    private static List<FilterCondition<UserField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE users\n" +
                "(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    username     VARCHAR(50) UNIQUE                   NOT NULL,\n" +
                "    salt         VARCHAR(255)                         NOT NULL,\n" +
                "    password     VARCHAR(255)                         NOT NULL,\n" +
                "    type         ENUM ('Admin', 'Basic') NOT NULL,\n" +
                "    creationdate TIMESTAMP                            NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                ");");
        userDB = new UserDB(conn);
    }

    @Test
    @Order(1)
    public void testAdding() {
        User u1 = new User("aabcqwe", "pass1", "asd123", UserType.Admin, LocalDateTime.of(2020, 1, 1, 10, 0));
        User u2 = new User("bzxcasd", "pass2", "asd456", UserType.Admin, LocalDateTime.of(2011, 1, 1, 11, 0));
        User u3 = new User("azxcasd", "pass3", "asd789", UserType.Basic, LocalDateTime.of(2019, 1, 1, 12, 0));
        User u4 = new User("czxcasd", "pass4", "qwe123", UserType.Basic, LocalDateTime.of(2020, 1, 1, 13, 0));
        User u5 = new User("babcqwe", "pass5", "qwe456", UserType.Basic, LocalDateTime.of(2020, 1, 1, 14, 0));

        assertEquals(0, userDB.query(allFilter).size());
        userDB.add(u1);
        assertEquals(1, userDB.query(allFilter).size());
        userDB.add(u2);
        assertEquals(2, userDB.query(allFilter).size());
        userDB.add(u3);
        assertEquals(3, userDB.query(allFilter).size());
        userDB.add(u4);
        assertEquals(4, userDB.query(allFilter).size());
        userDB.add(u5);
        assertEquals(5, userDB.query(allFilter).size());
    }


    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<UserField>> filters, UserDB db) {
        List<User> users = db.query(filters);
        assertEquals(affected, users.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(User u : users) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }

    @Test
    @Order(2)
    public void testAddingAndDeleting() {
        List<User> users = userDB.query(allFilter);
        int size = users.size();
        assertEquals(5, size);

        List<FilterCondition<UserField>> filters = List.of(
                new FilterCondition<>(UserField.TYPE, Operator.EQUALS, UserType.Basic.name())
        );
        testQueryDeleteAddOnce(size, 3, filters, userDB);

        filters = List.of(
                new FilterCondition<>(UserField.TYPE, Operator.EQUALS, UserType.Admin.name())
        );
        testQueryDeleteAddOnce(size, 2, filters, userDB);

        filters = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, "aabcqwe")
        );
        testQueryDeleteAddOnce(size, 1, filters, userDB);

        filters = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, "czxcasd")
        );
        testQueryDeleteAddOnce(size, 1, filters, userDB);

        filters = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.LIKE, "%abc%")
        );
        testQueryDeleteAddOnce(size, 2, filters, userDB);

        filters = List.of(
                new FilterCondition<>(UserField.SALT, Operator.LIKE, "asd%"),
                new FilterCondition<>(UserField.USERNAME, Operator.LIKE, "%asd")
        );

        testQueryDeleteAddOnce(size, 2, filters, userDB);

        filters = List.of(
                new FilterCondition<>(UserField.SALT, Operator.LIKE, "asd%"),
                new FilterCondition<>(UserField.USERNAME, Operator.LIKE, "%asd"),
                new FilterCondition<>(UserField.TYPE, Operator.EQUALS, UserType.Basic.name())
        );
        testQueryDeleteAddOnce(size, 1, filters, userDB);


        testQueryDeleteAddOnce(size, size, allFilter, userDB);
    }

}
