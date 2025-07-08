package databases.implementations;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserAnswerField;
import objects.questions.Answer;
import objects.user.QuizResult;
import objects.user.UserAnswer;
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
public class UserAnswerDBTest {
    private static Connection conn;
    private static UserAnswerDB userAnswerDB;
    private static List<FilterCondition<UserAnswerField>> allFilter;




    @BeforeAll
    public static void setUp() throws SQLException {
        allFilter = new ArrayList<>();
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        Statement stmt = conn.createStatement();
        stmt.execute("create table user_answers\n" +
                "(\n" +
                "    id         int primary key auto_increment,\n" +
                "    questionid int         not null,\n" +
                "    resultid   int         not null,\n" +
                "    isstring   boolean     not null,\n" +
                "    jsondata   varchar(65535)\n" +
                "    -- foreign key (questionid) references questions (id),\n" +
                "    -- foreign key (resultid) references quiz_results (id)\n" +
                ");\n");
        userAnswerDB = new UserAnswerDB(conn);
    }

    public void testQueryDeleteAddOnce(int startSize, int affected, List<FilterCondition<UserAnswerField>> filters, UserAnswerDB db) {
        List<UserAnswer> userAnswers = db.query(filters);
        assertEquals(affected, userAnswers.size());
        assertEquals(affected, db.delete(filters));
        assertEquals(startSize - affected, db.query(allFilter).size());
        for(UserAnswer u : userAnswers) db.add(u);
        assertEquals(startSize, db.query(allFilter).size());
    }


    @Test
    @Order(1)
    public void testAdding() {
        Answer<Integer> ans1 = new Answer<>(List.of(1,2,3,4));
        Answer<Integer> ans2 = new Answer<>(List.of(2,4,5,6));
        Answer<String> ans3 = new Answer<>(List.of("1", "3", "45", "56"));
        UserAnswer ua1 = new UserAnswer(1,2,false, ans1);
        UserAnswer ua2 = new UserAnswer(2,4,false, ans2);
        UserAnswer ua3 = new UserAnswer(3,4,true, ans3);
        assertEquals(0, userAnswerDB.query(allFilter).size());
        userAnswerDB.add(ua1);
        assertEquals(1, ua1.getId());
        assertEquals(1, userAnswerDB.query(allFilter).size());
        userAnswerDB.add(ua2);
        assertEquals(2, ua2.getId());
        assertEquals(2, userAnswerDB.query(allFilter).size());
        userAnswerDB.add(ua3);
        assertEquals(3, ua3.getId());
        assertEquals(3, userAnswerDB.query(allFilter).size());
    }

    @Test
    @Order(2)
    public void testQueryDeleteAdd() {
        List<UserAnswer> userAnswers = userAnswerDB.query(allFilter);
        int size = userAnswers.size();
        assertEquals(3, size);

        List<FilterCondition<UserAnswerField>> ls = List.of(
                new FilterCondition<>(UserAnswerField.QUESTIONID, Operator.EQUALS, 1)
        );

        testQueryDeleteAddOnce(3, 1, ls, userAnswerDB);

        ls = List.of(
                new FilterCondition<>(UserAnswerField.RESULTID, Operator.MOREEQ, 4)
        );
        testQueryDeleteAddOnce(3, 2, ls, userAnswerDB);

        testQueryDeleteAddOnce(3, 3, allFilter, userAnswerDB);

    }
}
