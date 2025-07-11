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
        String userIdStr = request.getSession().getAttribute("userid")==null?null:request.getSession().getAttribute("userid").toString();
        Integer userId = userIdStr==null?null:Integer.parseInt(userIdStr);

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
            json.addProperty("userId", userId);
            List<User> userList = userDB.query(List.of(new FilterCondition<>(UserField.ID, Operator.EQUALS, userId)));
            if (!userList.isEmpty() && userList.get(0).getType().toString().equalsIgnoreCase("ADMIN")) {
                json.addProperty("isAdmin", true);
            } else {
                json.addProperty("isAdmin", false);
            }
        } else {
            json.addProperty("isAdmin", false);
        }




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
                .filter(qr -> qr.getTimeTaken() >= 0)  // Only completed attempts
                .mapToDouble(QuizResult::getTotalScore)
                .average()
                .orElse(0);

        json.addProperty("averageScore", avgScore);



        if (userId != null) {
            List<QuizResult> ongoing = quizResultDB.query(List.of(
                    new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                    new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                    new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.LESS, 0)
            ));
            json.addProperty("ongoingResult", !ongoing.isEmpty());
            if(!ongoing.isEmpty()) {
                QuizResult qr = ongoing.getFirst();
                json.addProperty("ongoingPractice", qr.getTimeTaken() == -2);
                json.addProperty("ongoingResultId", qr.getId());
            }
        }

        response.getWriter().write(json.toString());
    }
}
