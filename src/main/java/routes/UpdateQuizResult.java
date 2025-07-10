package routes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuizResultDB;
import objects.user.QuizResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static utils.Constants.QUIZRESULTDB;

@WebServlet("/updatequizresult")
public class UpdateQuizResult extends HttpServlet {


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        int quizResultId = Integer.parseInt(request.getParameter("quizresultid"));
        int userId = Integer.parseInt(request.getParameter("userid"));
        int totalScore = Integer.parseInt(request.getParameter("totalScore"));
        int timeTaken = Integer.parseInt(request.getParameter("timeTaken"));
        boolean practice = Boolean.parseBoolean(request.getParameter("practice"));


        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        quizResultDB.updateQuizResult(quizResultId, timeTaken, totalScore);

        JsonObject json = new JsonObject();
        json.addProperty("success", true);

        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
