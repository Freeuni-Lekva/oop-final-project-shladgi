package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.implementations.*;
import objects.Quiz;
import objects.user.QuizResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/startQuiz")
public class StartQuizServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        String idStr = request.getParameter("id");
        String practiceStr = request.getParameter("practice");
        String userIdStr = request.getSession().getAttribute("userid")==null?null:request.getSession().getAttribute("userid").toString();
        Integer userId = userIdStr==null?null:Integer.parseInt(userIdStr);


        if (userId == null) {
            json.addProperty("success", false);
            json.addProperty("message", "User not logged in.");
            response.getWriter().write(json.toString());
            return;
        }

        if (idStr == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing quiz ID.");
            response.getWriter().write(json.toString());
            return;
        }

        int quizId = Integer.parseInt(idStr);
        boolean practice = Boolean.parseBoolean(practiceStr);

        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);


        // Check quiz exists
        List<Quiz> quizzes = quizDB.query(
                new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
        );
        if (quizzes.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "Quiz not found.");
            response.getWriter().write(json.toString());
            return;
        }

        Quiz quiz = quizzes.get(0);


        // Check practice mode allowed, es arasdros unda moxdes.
        if (practice && !quiz.isPracticeMode()) {
            json.addProperty("success", false);
            json.addProperty("message", "Practice mode is not available for this quiz.");
            response.getWriter().write(json.toString());
            return;
        }

        // Check if user has unfinished quiz
        List<FilterCondition<QuizResultField>> unfinishedFilters = List.of(
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.EQUALS, -1)
        );

        List<QuizResult> unfinishedResults = quizResultDB.query(unfinishedFilters);

        if (!unfinishedResults.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "You have an unfinished quiz. Please finish it before starting a new one.");
            response.getWriter().write(json.toString());
            return;
        }

        // Start new result entry
        QuizResult newResult = new QuizResult(
                userId,
                quizId,
                LocalDateTime.now(),
                -1.0,
                -1
        );

        quizResultDB.add(newResult);

        json.addProperty("success", true);
        json.addProperty("quizId", quizId);
        response.getWriter().write(json.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.getRequestDispatcher("quizPage.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}







