package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserAnswerField;
import databases.implementations.QuizResultDB;
import databases.implementations.UserAnswerDB;
import databases.implementations.UserDB;
import objects.user.QuizResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/delete-user-answers-for-result")
public class DeleteUserAnswersForResultServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setContentType("application/json");
        JsonObject json = new JsonObject();
        System.out.println("I AM HERE");
        String resultIdStr = req.getParameter("resultId");
        Integer userId = (Integer) req.getSession().getAttribute("userid");

        if (resultIdStr == null || userId == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing resultId or not logged in.");
            resp.getWriter().write(json.toString());
            return;
        }

        int resultId;
        try {
            resultId = Integer.parseInt(resultIdStr);
        } catch (NumberFormatException e) {
            json.addProperty("success", false);
            json.addProperty("message", "Invalid resultId.");
            resp.getWriter().write(json.toString());
            return;
        }

        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        UserAnswerDB userAnswerDB = (UserAnswerDB) getServletContext().getAttribute(USERANSWERDB);


        // Step 1: Check that this result belongs to the current user
        List<FilterCondition<QuizResultField>> resultFilters = List.of(
                new FilterCondition<>(QuizResultField.ID, Operator.EQUALS, resultId)
        );

        List<QuizResult> results = quizResultDB.query(resultFilters);
        if (results.isEmpty() || results.get(0).getUserId() != userId) {
            json.addProperty("success", false);
            json.addProperty("message", "You do not have permission to delete this quiz result.");
            resp.getWriter().write(json.toString());
            return;
        }

        // Step 2: Delete user answers with that resultId
        List<FilterCondition<UserAnswerField>> filters = List.of(
                new FilterCondition<>(UserAnswerField.RESULTID, Operator.EQUALS, resultId)
        );

        int deletedCount = userAnswerDB.delete(filters);

        json.addProperty("success", true);
        json.addProperty("deleted", deletedCount);
        resp.getWriter().write(json.toString());
    }
}
