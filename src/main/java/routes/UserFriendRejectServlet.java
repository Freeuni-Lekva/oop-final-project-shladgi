package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendRequestField;
import databases.filters.fields.UserField;
import databases.implementations.FriendRequestDB;
import databases.implementations.FriendshipDB;
import databases.implementations.UserDB;
import objects.user.Friendship;
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

@WebServlet("/friend-request/reject")
public class UserFriendRejectServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB);
        String sender = (String) request.getSession().getAttribute("username");
        String receiver =  request.getParameter("target");

        List<User> u1 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, sender));
        List<User> u2 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, receiver));
        User user1 = u1.get(0);
        User user2 = u2.get(0);

        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);
        friendRequestDB.delete(new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, user1.getId()),  new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, user2.getId()));
    }
}
