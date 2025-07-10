package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserDB;
import objects.user.QuizResult;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;
@WebServlet("/user_statistics")
public class UserStatisticsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        String username = request.getParameter("username");

        List<User> u = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if (u.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
            return;
        }
        User user = u.get(0);

        List<QuizResult> quizResults = quizResultDB.query(
                new FilterCondition<>(QuizResultField.USERID, Operator.MOREEQ, user.getId())
        );

        if(quizResults.isEmpty()){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Integer> ids = new ArrayList<>();
        for (QuizResult q : quizResults) {
            ids.add(q.getId());
        }

        Gson gson = new Gson();
        String json = gson.toJson(ids);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}

