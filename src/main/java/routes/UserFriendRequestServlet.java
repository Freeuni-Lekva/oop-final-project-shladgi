package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendRequestField;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utils.Constants.FRIENDREQUESTDB;
import static utils.Constants.USERDB;

@WebServlet("/user-friend-request")
public class UserFriendRequestServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        FriendRequestDB friendRequestDB = (FriendRequestDB) getServletContext().getAttribute(FRIENDREQUESTDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);

        String username = request.getParameter("username");
        List<User> users = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));

        if (users.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User u = users.get(0);
        List<FriendRequest> friendRequest = friendRequestDB.query(new FilterCondition<>(FriendRequestField.SECONDID, Operator.EQUALS, u.getId()));

        List<String> list =  new ArrayList<>();

        for (FriendRequest f : friendRequest) {
            List<User> us = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, f.getFirstId()));
            if(us.isEmpty()){
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            User u1 = us.get(0);
            list.add(u1.getUserName());
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = new Gson().toJson(list);
        response.getWriter().write(json);
    }
}
