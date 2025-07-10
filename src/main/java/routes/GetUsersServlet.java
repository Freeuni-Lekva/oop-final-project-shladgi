package routes;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@WebServlet("/users")
public class GetUsersServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.getRequestDispatcher("/users.html").forward(req, res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

    }
}


