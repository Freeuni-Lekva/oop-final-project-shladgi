package routes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendshipField;
import databases.filters.fields.UserField;
import databases.implementations.FriendshipDB;
import databases.implementations.QuizDB;
import databases.implementations.UserDB;
import objects.user.User;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/users")
public class GetUsersServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.getRequestDispatcher("/users.html").forward(req, res);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletContext context = request.getServletContext();
        UserDB userDB = (UserDB) context.getAttribute(USERDB);
        QuizDB quizDB = (QuizDB) context.getAttribute(QUIZDB);
        FriendshipDB friendshipDB = (FriendshipDB) context.getAttribute(FRIENDSHIPDB);
        Object o =  request.getSession().getAttribute("userid");
        String userIdStr = o == null ? null: o.toString();
        int id = userIdStr==null || userIdStr.isEmpty() ? -1:Integer.parseInt(userIdStr);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Parse request body
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            Gson gson = new Gson();
            UserRequest userRequest = gson.fromJson(sb.toString(), UserRequest.class);

            // Validate request
            if (userRequest.page < 1) userRequest.page = 1;
            if (userRequest.pageSize < 1 || userRequest.pageSize > 100) userRequest.pageSize = 10;


            List<User> Allusers = userDB.query(
                    List.of(new FilterCondition<>(UserField.USERNAME,Operator.LIKE,"%"+userRequest.searchQuery+"%"),
                            new FilterCondition<>(UserField.ID,Operator.NOTEQ,id)),
                    UserField.USERNAME,true,userRequest.pageSize, (userRequest.page-1)*userRequest.pageSize
                    );
            List<User> users = new ArrayList<>();
            for(User user : Allusers) {
                if(userRequest.friendStatus.equals("all") ) {
                    users.add(user);
                    continue;
                }

                int userId = user.getId();
                int size = friendshipDB.query(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, Math.min(id,userId)) ,
                        new FilterCondition<>(FriendshipField.SECONDID,Operator.EQUALS,Math.max(id,userId))).size();

                if(userRequest.friendStatus.equals("friends") && size != 1) continue;
                else if(userRequest.friendStatus.equals("nonfriends") && size == 1)continue;
                users.add(user);
            }

            int totalCount = userDB.query(new FilterCondition<>(UserField.ID, Operator.NOTEQ,-1)).size();
            int totalPages = (int) Math.ceil((double) totalCount / userRequest.pageSize);


           JsonObject jo = new JsonObject();
           jo.addProperty("totalPages", totalPages);
            JsonArray ja = new JsonArray();
            for(User user : users) {
                JsonObject joUser = new JsonObject();
                joUser.addProperty("name", user.getUserName());
                joUser.addProperty("type", user.getType().toString());
                ja.add(joUser);
            }
            jo.add("users", ja);

            response.getWriter().write(jo.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
            out.flush();
        }

    }

private static class UserRequest {
    public int page;
    public int pageSize;
    public String searchQuery;
    public String friendStatus;
}

}
