package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuizResultDB;
import objects.user.QuizResult;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.QUIZRESULTDB;

@WebServlet("/getTopPerformers")
public class GetTopPerformersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        String quizIdStr = request.getParameter("quizId");
        String timeHoursStr = request.getParameter("timeHours");
        String amountStr = request.getParameter("amount");

        // Validate required parameters
        if (quizIdStr == null || amountStr == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing required parameters: quizId and amount are required.");
            response.getWriter().write(json.toString());
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdStr);
            int amount = Integer.parseInt(amountStr);

            // Validate amount
            if (amount <= 0) {
                json.addProperty("success", false);
                json.addProperty("message", "Amount must be greater than 0.");
                response.getWriter().write(json.toString());
                return;
            }

            QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);

            // Build filter conditions
            List<FilterCondition<QuizResultField>> filters = new ArrayList<>();
            filters.add(new FilterCondition<>(QuizResultField.QUIZID, Operator.EQUALS, quizId));
            filters.add(new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MORE, -1)); // Only completed attempts

            // Add time filter if specified
            if (timeHoursStr != null && !timeHoursStr.trim().isEmpty()) {
                try {
                    int timeHours = Integer.parseInt(timeHoursStr);
                    if (timeHours > 0) {
                        LocalDateTime timeThreshold = LocalDateTime.now().minusHours(timeHours);
                        filters.add(new FilterCondition<>(QuizResultField.CREATIONDATE, Operator.MOREEQ, timeThreshold.toString()));
                    }
                } catch (NumberFormatException e) {
                    json.addProperty("success", false);
                    json.addProperty("message", "Invalid timeHours parameter. Must be a valid number.");
                    response.getWriter().write(json.toString());
                    return;
                }
            }

            // Query top performers sorted by score (descending)
            List<QuizResult> topPerformers = quizResultDB.query(
                    filters,
                    QuizResultField.TOTALSCORE,
                    false, // descending order
                    amount,
                    null
            );

            // Build response
            JsonArray performersArray = new JsonArray();
            for (QuizResult result : topPerformers) {
                JsonObject performer = new JsonObject();
                performer.addProperty("userId", result.getUserId());
                performer.addProperty("score", result.getTotalScore());
                performer.addProperty("timeTaken", result.getTimeTaken());
                performer.addProperty("date", result.getCreationDate().toString());
                performersArray.add(performer);
            }

            json.addProperty("success", true);
            json.add("performers", performersArray);
            json.addProperty("totalCount", topPerformers.size());

            if (timeHoursStr != null && !timeHoursStr.trim().isEmpty()) {
                json.addProperty("timeFilter", timeHoursStr + " hours");
            } else {
                json.addProperty("timeFilter", "all time");
            }

        } catch (NumberFormatException e) {
            json.addProperty("success", false);
            json.addProperty("message", "Invalid parameter format. QuizId and amount must be valid numbers.");
        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", "Server error: " + e.getMessage());
        }

        response.getWriter().write(json.toString());
    }
}