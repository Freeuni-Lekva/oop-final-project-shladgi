package routes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import databases.implementations.QuizDB;
import objects.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static utils.Constants.QUIZDB;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/add-popular-quizzes")
public class GetTop10PopularQuizzesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        List<Quiz> quizzes = quizDB.getTopQuizzes(10);

        JsonArray jsonArray = new JsonArray();

        for (Quiz q : quizzes) {
            JsonObject quizJson = new JsonObject();
            quizJson.addProperty("id", q.getId());
            quizJson.addProperty("userId", q.getUserId());
            quizJson.addProperty("title", q.getTitle());
            quizJson.addProperty("creationTime", q.getCreationDate().toString());

            jsonArray.add(quizJson);
        }

        String json = jsonArray.toString();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
