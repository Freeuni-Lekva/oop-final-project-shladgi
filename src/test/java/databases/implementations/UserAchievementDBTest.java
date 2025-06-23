package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserAchievementField;
import databases.filters.fields.UserField;
import objects.user.User;
import objects.user.UserAchievement;
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
public class UserAchievementDBTest {
    private static Connection conn;
    private static UserAchievementDB userAchDB;
    private static List<FilterCondition<UserAchievementField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE user_achievements\n" +
                "(\n" +
                "    id            INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    userid        INT       NOT NULL,\n" +
                "    achievementid INT       NOT NULL,\n" +
                "    creationdate  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                "   -- FOREIGN KEY (userid) REFERENCES users (id),\n" +
                "   -- FOREIGN KEY (achievementid) REFERENCES achievements (id)\n" +
                ");");
        userAchDB = new UserAchievementDB(conn);
    }
    
    @Test
    @Order(1)
    public void testAdding() {
        UserAchievement u1 = new UserAchievement(1, 1, LocalDateTime.of(2020, 1, 1, 10, 0));
        UserAchievement u2 = new UserAchievement(1, 2, LocalDateTime.of(2011, 1, 1, 11, 0));
        UserAchievement u3 = new UserAchievement(2, 3, LocalDateTime.of(2019, 1, 1, 12, 0));
        UserAchievement u4 = new UserAchievement(1, 4, LocalDateTime.of(2020, 2, 1, 13, 0));
        UserAchievement u5 = new UserAchievement(2, 4, LocalDateTime.of(2020, 2, 1, 14, 0));
        UserAchievement u6 = new UserAchievement(3, 4, LocalDateTime.of(2020, 2, 1, 15, 0));

        Assertions.assertEquals(0, userAchDB.query(allFilter).size());

        userAchDB.add(u1);
        Assertions.assertEquals(1, userAchDB.query(allFilter).size());

        userAchDB.add(u2);
        Assertions.assertEquals(2, userAchDB.query(allFilter).size());

        userAchDB.add(u3);
        Assertions.assertEquals(3, userAchDB.query(allFilter).size());

        userAchDB.add(u4);
        Assertions.assertEquals(4, userAchDB.query(allFilter).size());

        userAchDB.add(u5);
        Assertions.assertEquals(5, userAchDB.query(allFilter).size());

        userAchDB.add(u6);
        Assertions.assertEquals(6, userAchDB.query(allFilter).size());
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<UserAchievementField>> filters, UserAchievementDB db) {
        List<UserAchievement> userAchievements = db.query(filters);
        assertEquals(affected, userAchievements.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(UserAchievement u : userAchievements) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<UserAchievement> userAchievements = userAchDB.query(allFilter);
        int size = userAchievements.size();
        assertEquals(6, size);
        
        List<FilterCondition<UserAchievementField>> filters = List.of(
                new FilterCondition<>(UserAchievementField.USERID, Operator.LESSEQ, 2)
        );
        testQueryDeleteAddOnce(size, 5, filters, userAchDB);
        
        filters = List.of(
                new FilterCondition<>(UserAchievementField.USERID, Operator.EQUALS, 2)
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(UserAchievementField.ACHIEVEMENTID, Operator.EQUALS, 4)
        );
        testQueryDeleteAddOnce(size, 3, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(UserAchievementField.ACHIEVEMENTID, Operator.EQUALS, 4),
                new FilterCondition<>(UserAchievementField.USERID, Operator.EQUALS, 1)
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(UserAchievementField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2020, 1, 1, 0, 0).toString())
        );
        testQueryDeleteAddOnce(size, 4, filters, userAchDB);
    }


}
