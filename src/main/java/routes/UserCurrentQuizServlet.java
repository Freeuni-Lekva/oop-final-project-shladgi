package routes;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserDB;
import objects.Quiz;
import objects.user.QuizResult;
import objects.user.User;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/current-quiz")
public class UserCurrentQuizServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

        String username = request.getParameter("username");

        List<User> us = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if (us.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User user = us.get(0);

        List<QuizResult> quizResults = quizResultDB.query(
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, user.getId()),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.LESS, 0)
        );

        Gson gson = new Gson();
        if(quizResults.isEmpty()) {
            String json = gson.toJson("no curr");
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
            return;
        }

        Quiz quiz =  quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizResults.get(0).getQuizId())).get(0);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",  quiz.getId());
        jsonObject.addProperty("title", quiz.getTitle());
        jsonObject.addProperty("description", quiz.getDescription());
        jsonObject.addProperty("createDate", quiz.getCreationDate().toString());
        jsonObject.addProperty("creatorName", quiz.getUserId());
        String json = gson.toJson(jsonObject);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
