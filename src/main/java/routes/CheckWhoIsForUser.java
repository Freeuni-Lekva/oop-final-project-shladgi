package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendRequestField;
import databases.filters.fields.FriendshipField;
import databases.filters.fields.UserField;
import databases.implementations.FriendRequestDB;
import databases.implementations.FriendshipDB;
import databases.implementations.UserDB;
import objects.user.FriendRequest;
import objects.user.Friendship;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/check")
public class CheckWhoIsForUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);
        FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB); // fix this key
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);

        String username = (String)request.getSession().getAttribute("username");
        String target = request.getParameter("target");
        if (username == "" || target == null) {
            Gson gson = new Gson();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            System.out.println("aqaa");
            response.getWriter().write(gson.toJson("guest"));
            return;
        }

        List<User> us1 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        List<User> us2 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, target));
        if (us1.isEmpty() || us2.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User(s) not found");
            return;
        }
        User u1 = us1.get(0);
        User u2 = us2.get(0);

        // Check friendship both ways
        List<Friendship> friends = friendshipDB.query(
                new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, u1.getId()),
                new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, u2.getId())
        );
        if (friends.isEmpty()) {
            friends = friendshipDB.query(
                    new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, u2.getId()),
                    new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, u1.getId())
            );
        }

        // Check friend requests
        List<FriendRequest> targetSentUser = friendRequestDB.query(
                new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, u1.getId()),
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, u2.getId())
        );

        List<FriendRequest> userSentTarget = friendRequestDB.query(
                new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, u2.getId()),
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, u1.getId())
        );

        String status = "nOne";

        if (!friends.isEmpty()) {
            status = "friends";
        } else if (!targetSentUser.isEmpty()) {
            status = "requested";
        } else if (!userSentTarget.isEmpty()) {
            status = "request";
        }

        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(status));
    }
}
