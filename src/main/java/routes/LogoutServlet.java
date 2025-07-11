package routes;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet",value="/logout")
public class LogoutServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        req.getSession().setAttribute("userid",null);
        req.getSession().setAttribute("username" ,null);
        req.getSession().setAttribute("type", null);
        Cookie cookie = new Cookie("rememberMe", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
        res.sendRedirect("/");
    }
}
