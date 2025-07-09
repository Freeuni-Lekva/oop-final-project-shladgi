package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.AnnouncementField;
import objects.Announcement;
import objects.user.Challenge;
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
public class AnnouncementDBTest {

    private static Connection conn;
    private static AnnouncementDB announcementDB;
    private static List<FilterCondition<AnnouncementField>> allFilter;


    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE announcements(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    imagelink    VARCHAR(1000),\n" +
                "    title        VARCHAR(255) NOT NULL,\n" +
                "    content      VARCHAR(6500) NOT NUll,\n" +
                "    author       VARCHAR(255) NOT NULL,\n" +
                "    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    --FOREIGN KEY (author) REFERENCES users (username)\n" +
                ")");
        announcementDB = new AnnouncementDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<AnnouncementField>> filters, AnnouncementDB db) {
        List<Announcement> announcements = db.query(filters);
        assertEquals(affected, announcements.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(Announcement u : announcements) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }

    @Test
    @Order(1)
    public void testAdding(){
        Announcement a1 = new Announcement("A1", "content1", "user1", "link", LocalDateTime.of(2020, 1, 20, 10, 1));
        Announcement a2 = new Announcement("A2", "content2", "user1", "link", LocalDateTime.of(2020, 2, 10, 10, 1));
        Announcement a3 = new Announcement("A3", "content3", "user2", "link", LocalDateTime.of(2020, 2, 5, 10, 1));
        Announcement a4 = new Announcement("A4", "content4", "user1", "link", LocalDateTime.of(2020, 3, 6, 10, 1));
        Announcement a5 = new Announcement("A5", "content5", "user2", "link", LocalDateTime.of(2020, 3, 5, 10, 1));
        Announcement a6 = new Announcement("A6", "content6", "user3", "link", LocalDateTime.of(2020, 3, 6, 10, 1));

        assertEquals(0, announcementDB.query(allFilter).size());
        announcementDB.add(a1);
        assertEquals(1, announcementDB.query(allFilter).size());
        announcementDB.add(a2);
        assertEquals(2, announcementDB.query(allFilter).size());
        announcementDB.add(a3);
        assertEquals(3, announcementDB.query(allFilter).size());
        announcementDB.add(a4);
        assertEquals(4, announcementDB.query(allFilter).size());
        announcementDB.add(a5);
        assertEquals(5, announcementDB.query(allFilter).size());
        announcementDB.add(a6);
        assertEquals(6, announcementDB.query(allFilter).size());

        assertEquals(2, announcementDB.query("WHERE author='user2'").size());
    }

    @Test
    @Order(3)
    public void testQueryDeleteAdd(){
        List<Announcement> results = announcementDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<AnnouncementField>> filters = List.of(
            new FilterCondition<>(AnnouncementField.TITLE, Operator.EQUALS, "A1")
        );
        testQueryDeleteAddOnce(6, 1, filters, announcementDB);


        filters = List.of(
                new FilterCondition<>(AnnouncementField.TITLE, Operator.LIKE, "A%"),
                new FilterCondition<>(AnnouncementField.CREATIONDATE, Operator.MORE, LocalDateTime.of(2020,2,25,10,1).toString())
        );

        testQueryDeleteAddOnce(6, 3, filters, announcementDB);

        testQueryDeleteAddOnce(6,6,allFilter,announcementDB);
    }
}
