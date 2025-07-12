package databases.implementations;

import databases.DataBase;
import databases.filters.fields.QuizResultField;
import objects.user.QuizResult;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDB extends DataBase<QuizResult, QuizResultField> {
    public QuizResultDB(Connection connection) {super(connection, QuizResult.class);}


    public List<String> getTop10People() {
        List<String> list = new ArrayList<>();
        String sql = """
        SELECT u.username
        FROM quiz_results qr
        JOIN users u ON qr.userid = u.id
        GROUP BY u.username
        ORDER BY COUNT(*) DESC
        LIMIT 3
    """;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                list.add(rs.getString("username"));
             }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public boolean  updateQuizResult(int id, int timeTaken, double totalScore){
        String sql = "UPDATE quiz_results SET timetaken = ?, totalscore = ? WHERE id = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, timeTaken);
            stmt.setDouble(2, totalScore);
            stmt.setInt(3, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }


    }
}