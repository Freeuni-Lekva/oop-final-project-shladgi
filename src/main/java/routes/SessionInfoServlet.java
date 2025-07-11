package routes;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import objects.user.UserType;

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
        HttpSession session = request.getSession();
        String key = (String)request.getParameter("key");
        Object value = session.getAttribute(key);
        //this might be userid, username or type
        JsonObject json = new JsonObject();

        if(value == null){
            json.addProperty("value", "");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        if (value instanceof String) {
            json.addProperty("value", (String) value);
        } else if (value instanceof Integer) {
            json.addProperty("value", (Integer) value);
        } else if (value instanceof Boolean) {
            json.addProperty("value", (Boolean) value);
        } else if (value instanceof Long) {
            json.addProperty("value", (Long) value);
        } else if (value instanceof Double) {
            json.addProperty("value", (Double) value);
        } else if(value instanceof UserType){
            json.addProperty("value", value.toString());
        } else{
            throw new RuntimeException("Unknown variable type in session");
        }


        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}

