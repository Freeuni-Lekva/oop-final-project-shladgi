package servlets.userPageServlet;

import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import databases.implementations.FriendshipDB;

@WebServlet("/user-friends")
public class UserFriendsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute("firendShipDB");
        List<Integer> list = friendshipDB.getFriends(Integer.parseInt(request.getParameter("id")));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new Gson();
        String json = gson.toJson(list);

        response.getWriter().write(json);
    }
}
