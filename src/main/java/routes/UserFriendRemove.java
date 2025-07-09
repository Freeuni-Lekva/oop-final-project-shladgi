package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendshipField;
import databases.filters.fields.UserField;
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
import java.util.List;

import static utils.Constants.FRIENDSHIPDB;
import static utils.Constants.USERDB;

@WebServlet("/friend-remove")
public class UserFriendRemove extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("friend-remove");
        FriendshipDB friendship = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);

        String user1 = (String) request.getSession().getAttribute("username");
        String user2 = request.getParameter("target");
        System.out.println("user1: " + user1);
        System.out.println("user2: " + user2);
        List<User> u1 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, user1));
        List<User> u2 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, user2));
        User us1 = u1.get(0);
        User us2 = u2.get(0);

        if (u1.isEmpty() || u2.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        friendship.delete(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, us1.getId()), new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, us2.getId()));
    }
}
