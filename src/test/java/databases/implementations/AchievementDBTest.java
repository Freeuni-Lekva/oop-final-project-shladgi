package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.fields.AchievementField;
import objects.user.Achievement;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AchievementDBTest {
    private static Connection conn;
    private static AchievementDB userAchDB;
    private static List<FilterCondition<AchievementField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE achievements\n" +
                "(\n" +
                "    id       INT PRIMARY KEY,\n" +
                "    title    VARCHAR(255) NOT NULL,\n" +
                "    description VARCHAR(255),\n" +
                "    iconlink VARCHAR(255),\n" +
                "    rarity   ENUM ('Common', 'Uncommon', 'Rare', 'Epic', 'Legendary') NOT NULL\n" +
                ");");
        userAchDB = new AchievementDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<AchievementField>> filters, AchievementDB db) {
        List<Achievement> achievements = db.query(filters);
        assertEquals(affected, achievements.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(Achievement u : achievements) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }
}
