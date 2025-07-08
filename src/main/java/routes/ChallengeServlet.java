package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.*;
import objects.Quiz;
import objects.user.Challenge;
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

@WebServlet("/challenge")
public class ChallengeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        String friendUsername = request.getParameter("FriendUsername");
        String quizTitle = request.getParameter("QuizTitle");

        //DAOs
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        ChallengeDB challengeDB = (ChallengeDB) getServletContext().getAttribute(CHALLENGEDB);
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);

        Integer userId = (Integer) request.getSession().getAttribute("userid");
        if (userId == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Not logged in.");
            response.getWriter().write(json.toString());
            return;
        }


        List<User> curUser = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, userId));
        //es arasdros ar unda moxdes rom sesiidan araswori ID amovige
        if(curUser.isEmpty()){
            json.addProperty("success", false);
            json.addProperty("message", "User not found in database.");
            response.getWriter().write(json.toString());
            return;
        }
        User sender = curUser.get(0);

        if (friendUsername == null || quizTitle == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing data.");
            response.getWriter().write(json.toString());
            return;
        }


        // Check recipient user exists
        List<FilterCondition<UserField>> recipientFilter = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, friendUsername)
        );
        List<User> result = userDB.query(recipientFilter);
        User recipient = result.isEmpty() ? null : result.get(0);

        if (recipient == null) {
            json.addProperty("success", false);
            json.addProperty("message", "User not found.");
            response.getWriter().write(json.toString());
            return;
        }

        // Check friendship
        if (!friendshipDB.areFriends(sender.getId(), recipient.getId())) {
            json.addProperty("success", false);
            json.addProperty("message", "You are not friends with this user.");
            response.getWriter().write(json.toString());
            return;
        }

        // Check quiz exists
        List<Quiz> quizzes = quizDB.query(
                new FilterCondition<>(QuizField.TITLE, Operator.EQUALS, quizTitle)
        );
        if (quizzes.isEmpty()) {
            json.addProperty("success", false);
            json.addProperty("message", "Quiz not found.");
            response.getWriter().write(json.toString());
            return;
        }
        int quizId = quizzes.get(0).getId();

        // Get max score on quiz by sender
        List<FilterCondition<QuizResultField>> scoreFilters = List.of(
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, sender.getId()),
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId)
        );
        List<QuizResult> attempts = quizResultDB.query(scoreFilters);
        double maxScore = attempts.stream()
                .mapToDouble(QuizResult::getTotalScore)
                .max()
                .orElse(0.0);

        // Create and save the challenge
        Challenge challenge = new Challenge(
                quizId,
                sender.getId(),
                recipient.getId(),
                maxScore,
                quizTitle,
                LocalDateTime.now(),
                false
        );
        challengeDB.add(challenge);

        json.addProperty("success", true);
        response.getWriter().write(json.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // aq sxva html-unda.
            request.getRequestDispatcher("createNote.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
