package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.FriendRequestDB;
import databases.implementations.UserDB;
import objects.user.FriendRequest;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static utils.Constants.FRIENDREQUESTDB;
import static utils.Constants.USERDB;

@WebServlet("/friend-request/send")
public class UserWantsSendRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB);

        String username = (String) request.getSession().getAttribute("username");
        String target =  request.getParameter("target");

        List<User> us1 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS,  username));
        List<User> us2 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS,  target));
        User user1 =  us1.get(0);
        User user2 =  us2.get(0);

        FriendRequest req = new FriendRequest(user1.getId(), user2.getId(), LocalDateTime.now());

        friendRequestDB.add(req);
    }
}
