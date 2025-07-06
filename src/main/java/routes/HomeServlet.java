package routes;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "homeServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String currentUser = (String)request.getSession().getAttribute("username");
        if(currentUser != null) request.setAttribute("username", currentUser);
        try {
            request.getRequestDispatcher("/index.html").forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //TODO
        //handle login

        //request.getRequestDispatcher("/home.html").forward(request, response);

        System.out.println("dopost home");

    }
}
