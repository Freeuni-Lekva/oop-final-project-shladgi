package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.UserDB;
import objects.user.User;
import objects.user.UserType;
import utils.PasswordHasher;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet(name = "loginServlet", value = "/login")
public class LoginServlet extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/login.html").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        ServletContext context = request.getServletContext();
        UserDB userDB = (UserDB) context.getAttribute("UserDB");

        PasswordHasher hasher = new PasswordHasher();

        List<User> resultSet = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if(resultSet.isEmpty()
        || !hasher.verifyPassword(password, resultSet.getFirst().getSalt(), resultSet.getFirst().getPassword())){
            System.out.println("wrong");
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"wrong_username_or_password\"}");
            return;
        }

        request.getSession().setAttribute("userid", resultSet.getFirst().getId());

        response.setContentType("application/json");
        response.getWriter().write("{\"success\": true}");
    }


}
