package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendRequestField;
import databases.filters.fields.UserField;
import databases.implementations.FriendRequestDB;
import databases.implementations.UserDB;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/friend-request/reject")
public class UserFriendRejectServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
            FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB);

            String receiver = (String) request.getSession().getAttribute("username");
            String sender = request.getParameter("target");

            if (receiver == null || sender == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            List<User> u1 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, sender));
            List<User> u2 = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, receiver));

            if (u1.isEmpty() || u2.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            User user1 = u1.get(0);
            User user2 = u2.get(0);

            friendRequestDB.delete(
                    new FilterCondition<>(FriendRequestField.FIRSTID, Operator.EQUALS, user1.getId()),
                    new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, user2.getId())
            );

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

