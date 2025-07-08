package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.*;
import databases.implementations.*;
import objects.Quiz;
import objects.user.*;


import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/deleteQuiz")
public class DeleteQuizServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        Integer userId = (Integer) request.getSession().getAttribute("userid");
        // quiz id.
        String idStr = request.getParameter("id");

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

        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        UserAnswerDB userAnswerDB = (UserAnswerDB) getServletContext().getAttribute(USERANSWERDB);
        ChallengeDB challengeDB = (ChallengeDB) getServletContext().getAttribute(CHALLENGEDB);
        QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);

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

        List<User> users = userDB.query(
                new FilterCondition<>(UserField.ID, Operator.EQUALS, userId)
        );
        if (users.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "User not found.");
            response.getWriter().write(json.toString());
            return;
        }

        User user = users.get(0);
        boolean isAdmin = user.getType().toString().equalsIgnoreCase("ADMIN");

        if (quiz.getUserId() != userId && !isAdmin) {
            json.addProperty("success", false);
            json.addProperty("message", "You are not authorized to delete this quiz.");
            response.getWriter().write(json.toString());
            return;
        }

        // Delete from user_answers
        List<FilterCondition<QuizResultField>> resultFilter = List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId)
        );
        List<objects.user.QuizResult> results = quizResultDB.query(resultFilter);

        for (objects.user.QuizResult qr : results) {
            userAnswerDB.delete(List.of(
                    new FilterCondition<>(UserAnswerField.RESULTID, Operator.EQUALS, qr.getId())
            ));
        }

        // Delete related quiz results
        quizResultDB.delete(resultFilter);

        // Delete related challenges
        challengeDB.delete(List.of(
                new FilterCondition<>(ChallengeField.QUIZID, Operator.EQUALS, quizId)
        ));

        // DELETE QUESTION'S OF THIS QUIZ.
        questionDB.delete(List.of(
                new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, quizId)
        ));

        // Delete the quiz itself
        quizDB.delete(List.of(
                new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
        ));

        quizDB.delete(new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId));

        json.addProperty("success", true);
        json.addProperty("message", "Quiz deleted successfully.");
        response.getWriter().write(json.toString());
    }
}
