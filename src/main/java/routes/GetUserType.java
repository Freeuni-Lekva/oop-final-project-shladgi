package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.UserDB;
import objects.user.User;
import objects.user.UserType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.USERDB;
@WebServlet("/getUserType")
public class GetUserType extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println(11);
        String userName = request.getParameter("username");
        String userType = UserType.Basic.toString();
        System.out.println(11);
        if(userName != null && !userName.isEmpty()) {
            System.out.println(22);
            UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
            List<User> users= userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS,userName));
            System.out.println(22);
            if(users.size() > 0) {
                userType= users.getFirst().getType().toString();
            }

        }
        System.out.println(11);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JsonObject json = new JsonObject();
        System.out.println(11);
        json.addProperty("type", userType);
        response.getWriter().write(json.toString());
        System.out.println(11);

    }
}
