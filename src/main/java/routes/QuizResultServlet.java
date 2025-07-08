package routes;

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
        int quizResultId = Integer.parseInt(request.getParameter("quizResultId"));

        ServletContext context = getServletContext();

        QuizResultDB quizResultDB = (QuizResultDB) context.getAttribute(QUIZRESULTDB);

        List<QuizResult> quizResults = quizResultDB.query(new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizResultId));

        QuizResult quizResult = quizResults.getFirst();

        request.setAttribute("totalscore", quizResult.getTotalScore());
        request.setAttribute("timetaken", quizResult.getTimeTaken());
        request.setAttribute("creationdate", quizResult.getCreationDate());

        int quizId = quizResult.getQuizId();

        QuizDB quizDB = (QuizDB) context.getAttribute(QUIZDB);

        List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId));
        Quiz quiz = quizzes.getFirst();
        request.setAttribute("title", quiz.getTitle());


        try {
            request.getRequestDispatcher("/quizResultsPage.html").forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


    }


}

