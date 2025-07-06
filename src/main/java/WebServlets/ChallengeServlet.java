package WebServlets;

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

@WebServlet("/challenge")
public class ChallengeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String friendUsername = request.getParameter("FriendUsername");
        String quizTitle = request.getParameter("QuizTitle");

        // es loginisas unda chavagdo sesiashi wesit.
        User sender  = (User) request.getSession().getAttribute("user");

        if(friendUsername == null || quizTitle == null) {
            response.sendRedirect("ChallengeNote.html");
            return;
        }

        // amovige bazebi.
        UserDB userDB = (UserDB) getServletContext().getAttribute("userDB");
        ChallengeDB challengeDB = (ChallengeDB) getServletContext().getAttribute("challengeDB");
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute("quizResultDB");
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute("quizDB");
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute("friendshipDB");


        // unda shevamowmo valid-ia tu ara user da quiz rac chawera

        // Test-ebshi rac iyo eg wamovige da mushaobs tuara arvici jer
        List<FilterCondition<UserField>> recipientFilter = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, friendUsername)
        );
        List<User> result = userDB.query(recipientFilter);
        User recipient = result.isEmpty() ? null : result.get(0);

        if(recipient == null){
            //allert("wrong userName");
            response.sendRedirect("ChallengeNote.html");
            return;
        }else{
            // tu arsebobs axla gadavamowmot megobroba.
            // jer vergavige rogoraa chagdebuli bazashi kargad da mere davwer.
            //TO DO

        }


        int quizId = -1;
        List<Quiz> quizzes = quizDB.query(
                new FilterCondition<>(QuizField.TITLE, Operator.EQUALS, quizTitle)
        );
        if (!quizzes.isEmpty()) {
            quizId = quizzes.get(0).getId();
        }

        if(quizId == -1){
            // allert("wrong quiz Title");
            response.sendRedirect("ChallengeNote.html");
            return;
        }

        // Create filter for user+quiz combination
        List<FilterCondition<QuizResultField>> ScoreFilters = List.of(
          new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, sender.getId()),
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId)
        );

        // avige am user-is yvela mcdeloba am qvizis daweris.
        List<QuizResult> attempts = quizResultDB.query(ScoreFilters);

        // Calculate max score (default to 0 if no attempts),
        double maxScore = attempts.stream()
                .mapToDouble(QuizResult::getTotalScore)
                .max()
                .orElse(0.0);

        Challenge challenge = new Challenge(quizId,
                sender.getId(),
                recipient.getId(),
                maxScore,
                quizTitle,
                LocalDateTime.now());

        challengeDB.add(challenge);
        response.sendRedirect("index.html");
    }
}
