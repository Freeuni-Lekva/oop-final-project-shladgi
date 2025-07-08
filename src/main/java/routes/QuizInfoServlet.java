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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<User> creators = userDB.query(List.of(new FilterCondition<>(UserField.ID, Operator.EQUALS, quiz.getUserId())));
        if (!creators.isEmpty()) {
            User creator = creators.get(0);
            json.addProperty("creatorName", creator.getUserName());
            json.addProperty("creatorId", creator.getId());
        }

        // Authorization Info
        if (userId != null && userId == quiz.getUserId()) {
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



        // 1. User's past performance (sorted by date descending)
        List<FilterCondition<QuizResultField>> userResFilter = List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId)
        );
        List<QuizResult> userResults = userId == null ? List.of() :
                quizResultDB.query(userResFilter, QuizResultField.CREATIONDATE, false, null, null);

        JsonArray userAttemptsJson = new JsonArray();
        for (QuizResult qr : userResults) {
            JsonObject o = new JsonObject();
            o.addProperty("score", qr.getTotalScore());
            o.addProperty("timeTaken", qr.getTimeTaken());
            o.addProperty("date", qr.getCreationDate().toString());
            userAttemptsJson.add(o);
        }
        json.add("userAttempts", userAttemptsJson);



        // 2. Highest performers of all time (sorted by score descending)
        List<FilterCondition<QuizResultField>> topPerfFilter = List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MORE, -1)
        );
        List<QuizResult> topPerformers = quizResultDB.query(
                topPerfFilter, QuizResultField.TOTALSCORE, false, 10, null);

        JsonArray topPerformersJson = new JsonArray();
        for (QuizResult qr : topPerformers) {
            JsonObject o = new JsonObject();
            o.addProperty("userId", qr.getUserId());
            o.addProperty("score", qr.getTotalScore());
            o.addProperty("timeTaken", qr.getTimeTaken());
            o.addProperty("date", qr.getCreationDate().toString());
            topPerformersJson.add(o);
        }
        json.add("topPerformers", topPerformersJson);


        // 3. Top performers in the last day (sorted by score descending)
        LocalDateTime lastDay = LocalDateTime.now().minusDays(1);
        List<FilterCondition<QuizResultField>> recentTopFilter = List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.CREATIONDATE, Operator.MOREEQ, lastDay.toString()),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MORE, -1)
        );
        List<QuizResult> recentTopPerformers = quizResultDB.query(
                recentTopFilter, QuizResultField.TOTALSCORE, false, 10, null);


        JsonArray recentTopPerformersJson = new JsonArray();
        for (QuizResult qr : recentTopPerformers) {
            JsonObject o = new JsonObject();
            o.addProperty("userId", qr.getUserId());
            o.addProperty("score", qr.getTotalScore());
            o.addProperty("timeTaken", qr.getTimeTaken());
            o.addProperty("date", qr.getCreationDate().toString());
            recentTopPerformersJson.add(o);
        }
        json.add("recentTopPerformers", recentTopPerformersJson);


        // 4. Recent test takers (sorted by date descending)
        List<FilterCondition<QuizResultField>> recentTakersFilter = List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MORE, -1)
        );
        List<QuizResult> recentTakers = quizResultDB.query(
                recentTakersFilter, QuizResultField.CREATIONDATE, false, 20, null);

        JsonArray recentTakersJson = new JsonArray();
        for (QuizResult qr : recentTakers) {
            JsonObject o = new JsonObject();
            o.addProperty("userId", qr.getUserId());
            o.addProperty("score", qr.getTotalScore());
            o.addProperty("timeTaken", qr.getTimeTaken());
            o.addProperty("date", qr.getCreationDate().toString());
            recentTakersJson.add(o);
        }
        json.add("recentTakers", recentTakersJson);




        // Stats

        // Get all results for this quiz to calculate statistics
        List<QuizResult> allResults = quizResultDB.query(List.of(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId)
        ));

        // Calculate statistics using streams
        double avgScore = allResults.stream()
                .filter(qr -> qr.getTimeTaken() != -1)  // Only completed attempts
                .mapToDouble(QuizResult::getTotalScore)
                .average()
                .orElse(0);

        json.addProperty("averageScore", avgScore);

        response.getWriter().write(json.toString());
    }
}
