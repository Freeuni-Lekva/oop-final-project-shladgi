package routes;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuestionField;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserAnswerField;
import databases.implementations.QuestionDB;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserAnswerDB;
import objects.Quiz;
import objects.questions.Question;
import objects.user.QuizResult;
import objects.user.UserAnswer;

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

    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/quizResultsPage.html").forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int quizResultId = Integer.parseInt(request.getParameter("id"));
        ServletContext context = getServletContext();
        QuizResultDB quizResultDB = (QuizResultDB) context.getAttribute(QUIZRESULTDB);

        List<QuizResult> quizResults = quizResultDB.query(
                new FilterCondition<>(QuizResultField.ID, Operator.EQUALS, quizResultId));

        if(quizResults.isEmpty()){
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "Quiz result not found");
            response.getWriter().write(json.toString());
            return;
        }
        QuizResult quizResult = quizResults.getFirst();

        int quizId = quizResult.getQuizId();

        QuizDB quizDB = (QuizDB) context.getAttribute(QUIZDB);
        List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId));
        Quiz quiz = quizzes.getFirst();

        // Get all questions and user answers
        QuestionDB questionDB = (QuestionDB) context.getAttribute(QUESTIONDB);
        UserAnswerDB userAnswerDB = (UserAnswerDB) context.getAttribute(USERANSWERDB);

        List<Question> questions = questionDB.query(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, quizId));
        List<UserAnswer> userAnswers = userAnswerDB.query(
                new FilterCondition<>(UserAnswerField.RESULTID, Operator.EQUALS, quizResultId));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("title", quiz.getTitle());
        responseJson.addProperty("totalscore", quizResult.getTotalScore());
        responseJson.addProperty("timetaken", quizResult.getTimeTaken());
        responseJson.addProperty("creationdate", quizResult.getCreationDate().toString());
        responseJson.addProperty("userid", quizResult.getUserId());
        responseJson.addProperty("quizid", quizResult.getQuizId());

        // Create array for question-answer pairs
        JsonArray questionAnswerPairs = new JsonArray();

        for (Question question : questions) {
            JsonObject pair = new JsonObject();

            // Convert Question to JsonObject using Gson
            JsonObject questionJson = gson.toJsonTree(question).getAsJsonObject();

            // Manually add any additional fields that might not be included by default
            // (Gson should handle all standard fields automatically)
            pair.add("questionData", questionJson);

            // Find and add corresponding user answer
            for (UserAnswer answer : userAnswers) {
                if (answer.getQuestionId() == question.getId()) {
                    // Convert UserAnswer to JsonObject using Gson
                    JsonObject answerJson = gson.toJsonTree(answer).getAsJsonObject();
                    pair.add("userAnswer", answerJson);
                    break;
                }
            }

            questionAnswerPairs.add(pair);
        }

        responseJson.add("questionAnswerPairs", questionAnswerPairs);
        responseJson.addProperty("ok", true);
        response.getWriter().write(responseJson.toString());
    }
}