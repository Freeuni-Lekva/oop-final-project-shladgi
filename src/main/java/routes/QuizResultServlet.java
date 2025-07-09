package routes;

import com.google.gson.JsonObject;
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
import java.io.IOException;
import java.util.List;

import static utils.Constants.*;

@WebServlet(name = "quizResultServlet", value = "/quizResult")
public class QuizResultServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/quizResultsPage.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int quizResultId = Integer.parseInt(request.getParameter("quizResultId"));
        ServletContext context = getServletContext();
        QuizResultDB quizResultDB = (QuizResultDB) context.getAttribute(QUIZRESULTDB);

        List<QuizResult> quizResults = quizResultDB.query(
                new FilterCondition<>(QuizResultField.ID, Operator.EQUALS, quizResultId));
        QuizResult quizResult = quizResults.getFirst();

        int quizId = quizResult.getQuizId();

        QuizDB quizDB = (QuizDB) context.getAttribute(QUIZDB);
        List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId));
        Quiz quiz = quizzes.getFirst();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject json = new JsonObject();
        json.addProperty("title", quiz.getTitle());
        json.addProperty("totalscore", quizResult.getTotalScore());
        json.addProperty("timetaken", quizResult.getTimeTaken());
        json.addProperty("creationdate", quizResult.getCreationDate().toString());
        json.addProperty("userid", quizResult.getUserId());
        json.addProperty("quizid", quizResult.getQuizId());
        response.getWriter().write(json.toString());

    }


}

