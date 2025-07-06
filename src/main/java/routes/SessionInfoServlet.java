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

        Integer userId = (session != null) ? (Integer) session.getAttribute("userid") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (userId != null) {
            response.getWriter().write("{\"userid\": \"" + userId + "\"}");
        } else {
            response.getWriter().write("{\"userid\": null}");
        }
    }
}

