package routes;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet",value="/logout")
public class LogoutServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        System.out.println("Logoutvr");
        req.getSession().setAttribute("userid","");
        req.getSession().setAttribute("username" ,"");
        req.getSession().setAttribute("type", "");
        res.sendRedirect("index.html");
    }
}
