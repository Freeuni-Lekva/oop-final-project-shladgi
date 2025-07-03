package routes;

import databases.implementations.UserDB;
import objects.user.User;
import objects.user.UserType;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

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
        System.out.println("username: " + (username != null ? username : "null"));
        System.out.println("password: " + (password != null ? password : "null"));
        //TODO
        //handle login

        User u1 = new User("seetteerrisdcvsdcxc", "pass1", "asd123", UserType.Admin, LocalDateTime.of(2020, 1, 1, 10, 0));

        ServletContext context = request.getServletContext();
        UserDB userDB = (UserDB) context.getAttribute("UserDB");
        userDB.add(u1);
        System.out.println("user id : " + u1.getId());

        request.getSession().setAttribute("username", username);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        if(checkUser(username, password, request)) response.getWriter().write("{\"success\": true}");
        else response.getWriter().write("{\"success\": false}");



    }

    private boolean checkUser(String username, String password,  HttpServletRequest request){


        return true;
    }

}
