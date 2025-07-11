package routes;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuizResultDB;
import objects.user.QuizResult;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static utils.Constants.QUIZRESULTDB;

@WebServlet(name = "UserQuizResultsServlet", value = "/userQuizResults")
public class GetQuizResultIdsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(request.getParameter("userid") == null){
            System.out.println("userid nullll");
        }
        if(request.getParameter("quizid") == null){
            System.out.println("quizid nullll");
        }
        int userId = Integer.parseInt(request.getParameter("userid"));
        int quizId = Integer.parseInt(request.getParameter("quizid"));

        ServletContext context = getServletContext();
        QuizResultDB quizResultDB = (QuizResultDB) context.getAttribute(QUIZRESULTDB);

        List<FilterCondition<QuizResultField>> filter = List.of(
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MOREEQ, 0)

        );


        List<QuizResult> results = quizResultDB.query(
                filter,
                QuizResultField.CREATIONDATE,
                false,
                null,
                null
        );

        JsonObject json = new JsonObject();
        JsonArray idArray = new JsonArray();
        for (QuizResult result : results) {
            idArray.add(result.getId());
        }

        json.add("resultIds", idArray);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

