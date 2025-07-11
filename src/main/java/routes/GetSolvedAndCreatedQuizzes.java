package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserDB;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/solved-created-quizzes")
public class GetSolvedAndCreatedQuizzes extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

        String username = request.getParameter("username");

        List<User> users = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if (users.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User user = users.get(0);

        int solved = quizResultDB.query(
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, user.getId())
        ).size();

        int created = quizDB.query(
                new FilterCondition<>(QuizField.USERID, Operator.EQUALS, user.getId())
        ).size();

        JsonObject json = new JsonObject();
        json.addProperty("solved", solved);
        json.addProperty("created", created);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
}
