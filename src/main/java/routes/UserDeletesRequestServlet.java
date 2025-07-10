package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendRequestField;
import databases.filters.fields.UserField;
import databases.implementations.FriendRequestDB;
import databases.implementations.FriendshipDB;
import databases.implementations.UserDB;
import objects.user.FriendRequest;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/sent-request")
public class UserDeletesRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB);

        String username = (String) request.getSession().getAttribute("username");
        String receiver = request.getParameter("target");

        List<User> us1 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS,  username));
        List<User> us2 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS,  receiver));

        if (us1.isEmpty() || us2.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User user1 =  us1.get(0);
        User user2 =  us2.get(0);

        friendRequestDB.delete(
                new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, user1.getId()),
                new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, user2.getId())
        );
    }
}
