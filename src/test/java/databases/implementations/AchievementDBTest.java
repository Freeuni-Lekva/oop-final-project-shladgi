package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.AchievementField;
import objects.user.Achievement;
import objects.user.AchievementRarity;
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
                "    id       INT PRIMARY KEY AUTO_INCREMENT,\n" +
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

    @Test
    @Order(1)
    public void testAdding() {
        // Create Achievement objects with unique IDs and varied data
        Achievement ach1 = new Achievement("First Steps", "Complete your first quiz!", "icon1.png", AchievementRarity.Common);
        Achievement ach2 = new Achievement("Quiz Master", "Complete 10 quizzes!", "icon2.png", AchievementRarity.Legendary);
        Achievement ach3 = new Achievement("History Buff", "Score 100% on a history quiz.", "icon3.png", AchievementRarity.Rare);
        Achievement ach4 = new Achievement("Speed Demon", "Finish a quiz in record time.", "icon4.png", AchievementRarity.Epic);
        Achievement ach5 = new Achievement("Knowledge King", "Unlock all achievements.", "icon5.png", AchievementRarity.Legendary);
        Achievement ach6 = new Achievement("Early Bird", "Created an achievement before 9 AM.", "icon6.png", AchievementRarity.Common);


        assertEquals(0, userAchDB.query(allFilter).size());
        userAchDB.add(ach1);
        assertEquals(1, userAchDB.query(allFilter).size());
        userAchDB.add(ach2);
        assertEquals(2, userAchDB.query(allFilter).size());
        userAchDB.add(ach3);
        assertEquals(3, userAchDB.query(allFilter).size());
        userAchDB.add(ach4);
        assertEquals(4, userAchDB.query(allFilter).size());
        userAchDB.add(ach5);
        assertEquals(5, userAchDB.query(allFilter).size());
        userAchDB.add(ach6);
        assertEquals(6, userAchDB.query(allFilter).size());
    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<Achievement> results = userAchDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<AchievementField>> filters = List.of(
                new FilterCondition<>(AchievementField.RARITY, Operator.EQUALS, AchievementRarity.Common.name())
        );
        testQueryDeleteAddOnce(size, 2, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(AchievementField.RARITY, Operator.EQUALS, AchievementRarity.Epic.name())
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(AchievementField.TITLE, Operator.EQUALS, "Quiz Master")
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(AchievementField.TITLE, Operator.LIKE, "%Master%")
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(AchievementField.DESCRIPTION, Operator.LIKE, "%quiz%")
        );
        testQueryDeleteAddOnce(size, 4, filters, userAchDB);

        filters = List.of(
                new FilterCondition<>(AchievementField.RARITY, Operator.EQUALS, AchievementRarity.Rare.name())
        );
        testQueryDeleteAddOnce(size, 1, filters, userAchDB);



        testQueryDeleteAddOnce(size, size, allFilter, userAchDB);
    }
}
