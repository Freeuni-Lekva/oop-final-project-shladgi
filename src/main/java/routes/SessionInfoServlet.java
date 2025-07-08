package routes;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        String value = (String)request.getParameter("key");
        //this might be userid, username or type
        String answer = null;
        if(session.getAttribute(value) != null){
            answer = session.getAttribute(value).toString();
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println(answer);
        try {
            JsonObject json = new JsonObject();
            json.addProperty("value", answer);
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

