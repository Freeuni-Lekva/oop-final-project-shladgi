package servlets.userPageServlet;

import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.FriendshipDB;
import databases.implementations.UserDB;
import objects.user.AchievementRarity;
import objects.user.User;

import static utils.Constants.FRIENDSHIPDB;
import static utils.Constants.USERDB;

@WebServlet("/user-friends")
public class UserFriendsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        System.out.println("username: " + username);

        List<User> users = userDB.query(Collections.singletonList(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username)));
        User u = users.get(0);
        System.out.println("user id: " + u.getId());
        if (u == null) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("[]");
            return;
        }

        List<Integer> friendsListId = friendshipDB.getFriends(u.getId());

        List<String> friendsList =  new ArrayList<>();

        for(Integer i : friendsListId) {
            List<User> user = userDB.query(Collections.singletonList(new FilterCondition<>(UserField.ID, Operator.EQUALS, i)));
            User friend = user.get(0);
            System.out.println(friend.getUserName());
            friendsList.add(friend.getUserName());
        }

        if (friendsList == null) {
            friendsList = Collections.emptyList();
        }

        String json = new Gson().toJson(friendsList);
        response.getWriter().write(json);
    }
}
