package servlets.userPageServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;

@WebServlet("/user_friends")
public class UserFriendsServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> friends = Arrays.asList("Anna", "John", "David");
        HttpSession session = request.getSession();
        session.setAttribute("friends", friends);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        List<String> friends = (session != null) ? (List<String>) session.getAttribute("friends") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String json = new Gson().toJson(friends != null ? friends : Collections.emptyList());
        out.print(json);
        out.flush();
    }
}
