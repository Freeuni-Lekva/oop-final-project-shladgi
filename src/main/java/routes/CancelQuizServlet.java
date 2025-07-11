package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.*;
import databases.implementations.*;
import objects.user.QuizResult;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/cancelQuiz")
public class CancelQuizServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        String userIdStr = request.getSession().getAttribute("userid") == null ? null : request.getSession().getAttribute("userid").toString();
        Integer userId = userIdStr == null ? null : Integer.parseInt(userIdStr);
        String quizIdStr = request.getParameter("quizId");

        if (userId == null || quizIdStr == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing user or quiz ID.");
            response.getWriter().write(json.toString());
            return;
        }

        int quizId = Integer.parseInt(quizIdStr);

        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        UserAnswerDB userAnswerDB = (UserAnswerDB) getServletContext().getAttribute(USERANSWERDB);

        // Find the user's ongoing (unfinished) quiz result
        List<QuizResult> results = quizResultDB.query(List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.LESS, 0)
        ));

        if (results.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "No ongoing quiz found.");
            response.getWriter().write(json.toString());
            return;
        }

        QuizResult quizResult = results.get(0);

        // Delete user answers
        userAnswerDB.delete(List.of(
                new FilterCondition<>(UserAnswerField.RESULTID, Operator.EQUALS, quizResult.getId())
        ));

        // Delete quiz result itself
        quizResultDB.delete(List.of(
                new FilterCondition<>(QuizResultField.ID, Operator.EQUALS, quizResult.getId())
        ));

        json.addProperty("success", true);
        json.addProperty("message", "Quiz canceled successfully.");
        response.getWriter().write(json.toString());
    }
}
