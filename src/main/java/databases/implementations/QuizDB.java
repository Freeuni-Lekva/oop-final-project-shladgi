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

    public List<Quiz> getTopQuizzes(int limit) {
        List<Quiz> quizzes = new ArrayList<>();

        String sql = """
        SELECT q.id, q.title, q.userid, q.creationdate, q.totalscore, q.totalquestions,
               q.random, q.singlepage, q.immediatecorrection, q.practicemode, q.timelimit, q.description
        FROM quizzes q
        JOIN (
            SELECT quizid, COUNT(*) AS attempts
            FROM quiz_results
            GROUP BY quizid
            ORDER BY attempts DESC
            LIMIT ?
        ) topq ON q.id = topq.quizid
        ORDER BY topq.attempts DESC
    """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);  // set the limit parameter

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getInt("userid"),
                            rs.getTimestamp("creationdate").toLocalDateTime(),
                            rs.getInt("timelimit"),
                            rs.getDouble("totalscore"),
                            rs.getInt("totalquestions"),
                            rs.getBoolean("random"),
                            rs.getBoolean("singlepage"),
                            rs.getBoolean("immediatecorrection"),
                            rs.getBoolean("practicemode"),
                            rs.getString("description")
                    );
                    quizzes.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quizzes;
    }

    public List<Quiz> getMostRecentQuizzes(int limit) {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quizzes ORDER BY creationdate DESC LIMIT ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Quiz quiz = new Quiz();

                    quiz.setId(rs.getInt("id"));
                    quiz.setTitle(rs.getString("title"));
                    quiz.setUserId(rs.getInt("userid"));
                    quiz.setCreationDate(rs.getTimestamp("creationdate").toLocalDateTime());
                    quiz.setTimeLimit(rs.getInt("timelimit"));
                    quiz.setTotalScore(rs.getDouble("totalscore"));
                    quiz.setTotalQuestions(rs.getInt("totalquestions"));
                    quiz.setRandom(rs.getBoolean("random"));
                    quiz.setSinglePage(rs.getBoolean("singlepage"));
                    quiz.setImmediateCorrection(rs.getBoolean("immediatecorrection"));
                    quiz.setPracticeMode(rs.getBoolean("practicemode"));
                    quiz.setDescription(rs.getString("description"));

                    quizzes.add(quiz);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return quizzes;
    }
}
