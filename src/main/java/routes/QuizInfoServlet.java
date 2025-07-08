package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.*;
import objects.Quiz;
import objects.user.Note;
import objects.user.QuizResult;
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
        Integer userId = (Integer) request.getSession().getAttribute("userid");

        if (quizIdStr == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing quiz ID.");
            response.getWriter().write(json.toString());
            return;
        }

        int quizId = Integer.parseInt(quizIdStr);


        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);


        List<Quiz> quizzes = quizDB.query(List.of(
                new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
        ));

        /*if (quizzes.isEmpty()) {
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
        */

        if (quizzes.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "Quiz not found.");
            response.getWriter().write(json.toString());
            return;
        }

        Quiz quiz = quizzes.get(0);

        json.addProperty("success", true);
        json.addProperty("title", quiz.getTitle());
        json.addProperty("description", quiz.getDescription());
        json.addProperty("totalQuestions", quiz.getTotalQuestions());
        json.addProperty("totalScore", quiz.getTotalScore());
        json.addProperty("practiceAvailable", quiz.isPracticeMode());

        // Creator Info
        List<User> creators = userDB.query(List.of(new FilterCondition<>(UserField.ID, Operator.EQUALS, quiz.getCreatorId())));
        if (!creators.isEmpty()) {
            User creator = creators.get(0);
            json.addProperty("creatorName", creator.getUserName());
            json.addProperty("creatorId", creator.getId());
        }

        // Authorization Info
        if (userId != null && userId == quiz.getCreatorId()) {
            json.addProperty("isOwner", true);
        } else {
            json.addProperty("isOwner", false);
        }

        if (userId != null) {
            List<User> userList = userDB.query(List.of(new FilterCondition<>(UserField.ID, Operator.EQUALS, userId)));
            if (!userList.isEmpty() && userList.get(0).getType().toString().equalsIgnoreCase("ADMIN")) {
                json.addProperty("isAdmin", true);
            } else {
                json.addProperty("isAdmin", false);
            }
        } else {
            json.addProperty("isAdmin", false);
        }


        // Unsorted User Attempts, vinc shemosulia imena magis.
        List<QuizResult> userResults = (userId == null) ? List.of() : quizResultDB.query(List.of(
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId)
        ));

        JsonArray userAttemptsJson = new JsonArray();
        for (QuizResult qr : userResults) {
            JsonObject o = new JsonObject();
            o.addProperty("score", qr.getTotalScore());
            o.addProperty("timeTaken", qr.getTimeTaken());
            o.addProperty("date", qr.getCreationDate().toString());
            userAttemptsJson.add(o);
        }
        json.add("userAttempts", userAttemptsJson);



        // All-time top performers (unsorted)
        List<QuizResult> allTime = quizResultDB.query(List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId)
        ));

        JsonArray topPerformers = new JsonArray();
        for (QuizResult qr : allTime) {
            JsonObject o = new JsonObject();
            o.addProperty("userId", qr.getUserId());
            o.addProperty("score", qr.getTotalScore());
            topPerformers.add(o);
        }
        json.add("topPerformers", topPerformers);


        // Top performers in the last 24 hours
        LocalDateTime lastDay = LocalDateTime.now().minusDays(1);
        List<QuizResult> recent = quizResultDB.query(List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.CREATIONDATE, Operator.MOREEQ, lastDay.toString())
        ));

        JsonArray topLastDay = new JsonArray();
        for (QuizResult qr : recent) {
            JsonObject o = new JsonObject();
            o.addProperty("userId", qr.getUserId());
            o.addProperty("score", qr.getTotalScore());
            topLastDay.add(o);
        }
        json.add("recentTopPerformers", topLastDay);


        // Recent takers (any performance)
        // es igivea rac zeda all time, drois shezgudvaa dasamatebeli prosta.
        JsonArray recentTakers = new JsonArray();
        for (QuizResult qr : allTime) {
            JsonObject o = new JsonObject();
            o.addProperty("userId", qr.getUserId());
            o.addProperty("score", qr.getTotalScore());
            o.addProperty("timeTaken", qr.getTimeTaken());
            o.addProperty("date", qr.getCreationDate().toString());
            recentTakers.add(o);
        }
        json.add("recentTakers", recentTakers);


        // Stats
        double avg = allTime.stream().mapToDouble(QuizResult::getTotalScore).average().orElse(0);
        long finished = allTime.stream().filter(q -> q.getTimeTaken() != -1).count();

        json.addProperty("averageScore", avg);
        json.addProperty("completionRate", (allTime.size() == 0) ? 0 : (double) finished / allTime.size());

        response.getWriter().write(json.toString());
    }
}
