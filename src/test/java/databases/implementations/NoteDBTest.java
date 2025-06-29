package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.NoteField;
import objects.user.Note;
import objects.user.Note;
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
public class NoteDBTest {
    private static Connection conn;
    private static NoteDB noteDB;
    private static List<FilterCondition<NoteField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE notes\n" +
                "(\n" +
                "    id           INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    senderid     INT       NOT NULL,\n" +
                "    recipientid  INT       NOT NULL,\n" +
                "    creationdate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    text         TEXT      NOT NULL\n" +
                "    -- FOREIGN KEY (senderid) REFERENCES users (id),\n" +
                "    -- FOREIGN KEY (recipientid) REFERENCES users (id)\n" +
                ");");
        noteDB = new NoteDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<NoteField>> filters, NoteDB db) {
        List<Note> notes = db.query(filters);
        assertEquals(affected, notes.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(Note u : notes) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }

    @Test
    @Order(1)
    public void testAdding(){
        Note n1 = new Note(1, 2, LocalDateTime.of(2024, 1, 1, 10, 0), "Quiz 1");
        Note n2 = new Note(2, 3, LocalDateTime.of(2024, 1, 5, 11, 30), "Quiz 2");
        Note n3 = new Note(2, 1, LocalDateTime.of(2020, 2, 10, 12, 0), "Quiz 2");
        Note n4 = new Note(3, 4, LocalDateTime.of(2019, 2, 15, 13, 15), "Quiz 1");
        Note n5 = new Note(3, 4, LocalDateTime.of(2019, 3, 20, 14, 45), "Quiz 3");
        Note n6 = new Note(4, 2, LocalDateTime.of(2024, 4, 1, 9, 0), "Quiz 3");

        noteDB.add(n1);
        assertEquals(1, noteDB.query(allFilter).size());
        noteDB.add(n2);
        assertEquals(2, noteDB.query(allFilter).size());
        noteDB.add(n3);
        assertEquals(3, noteDB.query(allFilter).size());
        noteDB.add(n4);
        assertEquals(4, noteDB.query(allFilter).size());
        noteDB.add(n5);
        assertEquals(5, noteDB.query(allFilter).size());
        noteDB.add(n6);
        assertEquals(6, noteDB.query(allFilter).size());

    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<Note> results = noteDB.query(allFilter);
        int size = results.size();
        assertEquals(6, size);

        List<FilterCondition<NoteField>> filters = List.of(
                new FilterCondition<>(NoteField.SENDERID, Operator.EQUALS, 2),
                new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, 3)
        );
        testQueryDeleteAddOnce(size, 1, filters, noteDB);

        filters = List.of(
                new FilterCondition<>(NoteField.CREATIONDATE, Operator.MOREEQ, LocalDateTime.of(2024, 1, 1, 0, 0).toString()),
                new FilterCondition<>(NoteField.CREATIONDATE, Operator.LESS, LocalDateTime.of(2024, 1, 5, 0, 0).toString())
        );
        testQueryDeleteAddOnce(size, 1, filters, noteDB);

        filters = List.of(
                new FilterCondition<>(NoteField.CREATIONDATE, Operator.MOREEQ, LocalDateTime.of(2024, 1, 1, 0, 0).toString()),
                new FilterCondition<>(NoteField.CREATIONDATE, Operator.LESS, LocalDateTime.of(2024, 1, 5, 0, 0).toString()),
                new FilterCondition<>(NoteField.SENDERID, Operator.EQUALS, 2)
        );
        testQueryDeleteAddOnce(size, 0, filters, noteDB);

        filters = List.of(
                new FilterCondition<>(NoteField.SENDERID, Operator.MOREEQ, 2),
                new FilterCondition<>(NoteField.RECIPIENTID, Operator.LESSEQ, 3)
        );
        testQueryDeleteAddOnce(size, 3, filters, noteDB);

        testQueryDeleteAddOnce(size, size, allFilter, noteDB);
    }
}
