package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.*;
import databases.implementations.*;
import objects.Quiz;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/get-quiz-ids")
public class GetQuizIdsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //String type = (String)request.getSession().getAttribute("type");
        //System.out.println(type);
        /*if(!type.equals("admin")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }*/

        try {
            UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

            if (userDB == null || quizDB == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database not initialized");
                return;
            }

            String username = request.getParameter("username");
            List<User> users = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
            if (users.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                return;
            }
            User user = users.get(0);
            List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.USERID, Operator.EQUALS, user.getId()));

            List<Integer> ids = new ArrayList<>();
            for (Quiz quiz : quizzes) {
                ids.add(quiz.getId());
            }

            Gson gson = new Gson();
            String json = gson.toJson(ids);
            response.setContentType("application/json");
            response.getWriter().write(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error");
        }
    }

}

