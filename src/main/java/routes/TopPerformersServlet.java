package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.*;
import objects.Quiz;
import objects.user.Note;
import objects.user.QuizResult;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static utils.Constants.*;


@WebServlet("/api/topPerformers")
public class TopPerformersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            int quizId = Integer.parseInt(request.getParameter("quizId"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            Integer hours = request.getParameter("hours") != null ?
                    Integer.parseInt(request.getParameter("hours")) : null;

            QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);

            List<FilterCondition<QuizResultField>> filters = new ArrayList<>();
            filters.add(new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId));
            filters.add(new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MOREEQ, -1));

            // filtri rom shesabamisi drois nawerebi avigo
            if (hours != null) {
                LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
                filters.add(new FilterCondition<>(QuizResultField.CREATIONDATE, Operator.MOREEQ, cutoff.toString()));
            }

            List<QuizResult> results = quizResultDB.query(
                    filters,
                    QuizResultField.TOTALSCORE,
                    false,
                    limit,
                    null
            );

            JsonArray jsonArray = new JsonArray();
            for (QuizResult result : results) {
                JsonObject obj = new JsonObject();
                obj.addProperty("userId", result.getUserId());
                obj.addProperty("score", result.getTotalScore());
                obj.addProperty("timeTaken", result.getTimeTaken());
                jsonArray.add(obj);
            }

            response.setContentType("application/json");
            response.getWriter().write(jsonArray.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid request\"}");
        }
    }
}