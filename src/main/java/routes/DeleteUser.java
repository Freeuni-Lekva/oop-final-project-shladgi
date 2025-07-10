package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.*;
import databases.implementations.*;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/remove-user")
public class DeleteUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
            ChallengeDB challengeDB = (ChallengeDB) getServletContext().getAttribute(CHALLENGEDB);
            NoteDB noteDB = (NoteDB) getServletContext().getAttribute(NOTEDB);
            FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);
            FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB);
            UserAchievementDB userAchievementDB = (UserAchievementDB)  getServletContext().getAttribute(USERACHIEVEMENTDB);

            if (userDB == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database not initialized");
                return;
            }

            String username = request.getParameter("username");
            if (username == null || username.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username parameter");
                return;
            }

            List<User> l = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
            if (l.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }

            User user = l.get(0);

            friendshipDB.delete(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, user.getId()));
            friendshipDB.delete(new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, user.getId()));
            friendRequestDB.delete(new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, user.getId()));
            friendRequestDB.delete(new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, user.getId()));
            noteDB.delete(new FilterCondition<>(NoteField.SENDERID, Operator.EQUALS, user.getId()));
            noteDB.delete(new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, user.getId()));
            challengeDB.delete(new FilterCondition<>(ChallengeField.SENDERID, Operator.EQUALS, user.getId()));
            challengeDB.delete(new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, user.getId()));
            userAchievementDB.delete(new FilterCondition<>(UserAchievementField.USERID, Operator.EQUALS, user.getId()));
            userDB.delete(new FilterCondition<>(UserField.ID, Operator.EQUALS, user.getId()));

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}
