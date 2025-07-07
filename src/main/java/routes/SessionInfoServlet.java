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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String value = (String)request.getParameter("key");
        //this might be userid, username or type
        String answer = null;
        if(value!=null) answer = (session == null) ? null : session.getAttribute(value).toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write("{\"value\": \"" + (answer != null ? answer : "") + "\"}");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

