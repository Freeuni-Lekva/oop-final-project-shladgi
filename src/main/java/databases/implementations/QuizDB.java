package databases.implementations;

import databases.DataBase;
import databases.filters.fields.QuizField;
import objects.Quiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizDB extends DataBase<Quiz, QuizField> {
    public QuizDB(Connection connection) {super(connection, Quiz.class);}

    public List<Integer> get10RecentlyCreatedQuizzes() {
        List<Integer> quizzes = new ArrayList<>();
        String sql = """
        SELECT id
        FROM quizzes
        ORDER BY creationdate DESC
        LIMIT 10
    """;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                quizzes.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return quizzes;
    }

}
