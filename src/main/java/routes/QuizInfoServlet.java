package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.UserField;
import databases.implementations.NoteDB;
import databases.implementations.QuizDB;
import databases.implementations.UserDB;
import databases.implementations.FriendshipDB;
import objects.Quiz;
import objects.user.Note;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import static utils.Constants.*;

@WebServlet("/quizInfo")
public class QuizInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        String quizIdStr = request.getParameter("id");
        if (quizIdStr == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing quiz ID.");
            response.getWriter().write(json.toString());
            return;
        }

        int quizId = Integer.parseInt(quizIdStr);
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        List<Quiz> quizzes = quizDB.query(List.of(
                new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
        ));

        if (quizzes.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "Quiz not found.");
        } else {
            Quiz quiz = quizzes.get(0);
            json.addProperty("success", true);
            json.addProperty("title", quiz.getTitle());
            json.addProperty("totalQuestions", quiz.getTotalQuestions());
            json.addProperty("totalScore", quiz.getTotalScore());
            json.addProperty("practiceAvailable", quiz.isPracticeMode());
        }

        response.getWriter().write(json.toString());
    }
}
