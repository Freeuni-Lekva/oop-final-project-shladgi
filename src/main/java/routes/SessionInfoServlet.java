package routes;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/session-info")
public class SessionInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        String username = (session != null) ? (String) session.getAttribute("username") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (username != null) {
            response.getWriter().write("{\"username\": \"" + username + "\"}");
        } else {
            response.getWriter().write("{\"username\": null}");
        }
    }
}

