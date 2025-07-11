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

@WebServlet("/promoteUser")
public class PromoteUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Object o= req.getSession().getAttribute("type");
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);

        if (o == null || !o.toString().equalsIgnoreCase("Admin")) {
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "Unauthorized access.");
            res.getWriter().write(json.toString());
            return;
        }

        String username = req.getParameter("username");

        if(username == null|| username.isEmpty()) {
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "Username is required.");
            res.getWriter().write(json.toString());
            return;
        }

        List<User> users = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if(users.isEmpty()) {
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "User not found");
            res.getWriter().write(json.toString());
            return;
        }

        userDB.updateType(List.of(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username)), UserType.Admin);
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("message", "User successfully promoted.");
        res.getWriter().write(json.toString());




    }
}
