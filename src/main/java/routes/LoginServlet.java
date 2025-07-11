package routes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.filters.fields.UserTokenField;
import databases.implementations.UserDB;
import databases.implementations.UserTokenDB;
import objects.user.User;
import objects.user.UserToken;
import objects.user.UserType;
import utils.PasswordHasher;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static utils.Constants.*;

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
        boolean rememberMe = true ;


        ServletContext context = request.getServletContext();
        UserDB userDB = (UserDB) context.getAttribute(USERDB);

        PasswordHasher hasher = new PasswordHasher();

        List<User> resultSet = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if(resultSet.isEmpty()
        || !hasher.verifyPassword(password, resultSet.getFirst().getSalt(), resultSet.getFirst().getPassword())){
            response.setContentType("application/json");
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("error", "wrong_username_or_password");
            response.getWriter().write(json.toString());
            return;
        }

        User user = resultSet.get(0);
        if(rememberMe){
            UserTokenDB userTokenDB = (UserTokenDB) context.getAttribute(USERTOKENDB);
            userTokenDB.delete(new FilterCondition<>(UserTokenField.USERID, Operator.EQUALS, user.getId()));
            userTokenDB.delete(new FilterCondition<>(UserTokenField.EXPIREDATE, Operator.LESSEQ,LocalDateTime.now().toString()));

            String token = UUID.randomUUID().toString();
            UserToken ut = new UserToken(token,user.getId(), LocalDateTime.now().plusDays(1));
            userTokenDB.add(ut);
            Cookie cookie = new Cookie("rememberMe", token);
            cookie.setHttpOnly(true);
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            cookie.setPath("/");
            response.addCookie(cookie);
        }

        request.getSession().setAttribute("userid", user.getId());
        request.getSession().setAttribute("username",user.getUserName());
        request.getSession().setAttribute("type", user.getType());

        response.setContentType("application/json");
        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        response.getWriter().write(json.toString());
    }
}
