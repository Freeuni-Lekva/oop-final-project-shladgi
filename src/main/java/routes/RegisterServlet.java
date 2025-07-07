package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.UserDB;
import objects.user.User;
import objects.user.UserType;
import utils.PasswordHasher;

import javax.naming.Context;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet(name = "registerServlet", value = "/register")
public class RegisterServlet extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        try {
            request.getRequestDispatcher("/register.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        ServletContext context = getServletContext();

        UserDB userDB = (UserDB) context.getAttribute("UserDB");

        //if username already exists
        if(!userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username)).isEmpty()){
            response.setContentType("application/json");
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("error", "username_taken");
            response.getWriter().write(json.toString());
            return;
        }

        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hashedPassword = hasher.hashPassword(password, salt);

        User newUser = new User(username, hashedPassword, salt, UserType.Basic, LocalDateTime.now());

        userDB.add(newUser);

        HttpSession session = request.getSession();
        session.setAttribute("userid", newUser.getId());
        request.getSession().setAttribute("username", newUser.getUserName());
        request.getSession().setAttribute("type", newUser.getType());
        response.setContentType("application/json");

        response.getWriter().write("{\"success\": true}");
    }

}

