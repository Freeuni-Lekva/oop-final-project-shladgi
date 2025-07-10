package routes;

import com.google.gson.*;
import com.sun.net.httpserver.HttpContext;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuestionField;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuestionDB;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import objects.Quiz;
import objects.questions.Question;
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

import static utils.Constants.*;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){

        try {
            request.getRequestDispatcher("/quiz.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        String quizIdStr = request.getParameter("id");
        JsonObject json = new JsonObject();

        HttpSession session = request.getSession();

        if(session == null){
            json.addProperty("success", false);
            json.addProperty("message", "User not logged in!");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;

        }

        Integer userId = (Integer)session.getAttribute("userid");

        if(userId == null){
            json.addProperty("success", false);
            json.addProperty("message", "User not logged in!");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }


        if(session == null){
            json.addProperty("success", false);
            json.addProperty("message", "User not logged in!");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;

        }

        if (quizIdStr == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Quiz ID not provided");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        int quizId;
       try {
           quizId = Integer.parseInt(quizIdStr);
       }catch (Exception e){
           json.addProperty("success", false);
           json.addProperty("message", "Invalid Quiz ID");
           try {
               response.getWriter().write(json.toString());
           } catch (IOException r) {
               throw new RuntimeException(r);
           }
           return;
        }

        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

        List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId));

        if(quizzes.isEmpty()){
            json.addProperty("success", false);
            json.addProperty("message", "Quiz with this ID does not exist!");
            try {
                response.getWriter().write(json.toString());
            } catch (IOException r) {
                throw new RuntimeException(r);
            }
            return;
        }

        Quiz quiz = quizzes.getFirst();




        QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);
        List<Question> questions = questionDB.query(List.of(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, quizId)),
                QuestionField.ID,
                true,
                null,
                null);

        Gson gson = new Gson();

        JsonElement questionsJsonArray = gson.toJsonTree(questions); // this becomes a JsonArray

        json.addProperty("success", true);
        json.add("questions", questionsJsonArray);// add array to response object
        json.addProperty("title", quiz.getTitle());
        json.addProperty("totalscore", quiz.getTotalScore());
        json.addProperty("israndom", quiz.isRandom());
        json.addProperty("singlepage", quiz.isSinglePage());
        json.addProperty("totalquestions", quiz.getTotalQuestions());
        json.addProperty("immediatecorrection", quiz.isImmediateCorrection());
        json.addProperty("practicemode", quiz.isPracticeMode());
        json.addProperty("timelimit", quiz.getTimeLimit());
        json.addProperty("description", quiz.getDescription());

        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);

        List<QuizResult> quizResults = quizResultDB.query(
                new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId),
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.LESS, 0));

        QuizResult quizResult = quizResults.getFirst();

        json.addProperty("quizresultid", quizResult.getId());

        try {
            response.getWriter().write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
