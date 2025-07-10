package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuizResultDB;
import objects.user.QuizResult;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static utils.Constants.QUIZRESULTDB;

@WebServlet("/checkBeforeQuiz")
public class CheckingBeforeQuizServlet extends HttpServlet{

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){

        JsonObject json = new JsonObject();
        HttpSession session = request.getSession();
        if(session == null || session.getAttribute("userid") == null){

            json.addProperty("success", false);
            json.addProperty("message", "User not logged in!");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        int userId = (Integer)session.getAttribute("userid");

        int quizId = Integer.parseInt(request.getParameter("id"));

        boolean practice = Boolean.parseBoolean(request.getParameter("practice"));


        ServletContext context = getServletContext();

        QuizResultDB quizResultDB = (QuizResultDB) context.getAttribute(QUIZRESULTDB);


        if(practice){
            List<QuizResult> quizResults = quizResultDB.query(
                    new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                    new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                    new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.EQUALS, -2));
            if(quizResults.isEmpty()){

                json.addProperty("success", false);
                json.addProperty("message", "notStarted");
                try {
                    response.getWriter().write(json.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

        }else {
            List<QuizResult> quizResults = quizResultDB.query(
                    new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                    new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                    new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.EQUALS, -1));
            if(quizResults.isEmpty()){
                json.addProperty("success", false);
                json.addProperty("message", "notStarted");
                try {
                    response.getWriter().write(json.toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }

        try {
            json.addProperty("success", true);
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
