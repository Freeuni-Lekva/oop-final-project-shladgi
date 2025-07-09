package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.implementations.QuestionDB;
import databases.implementations.QuizDB;
import objects.Quiz;
import objects.questions.QType;
import objects.questions.QuestionSingleChoice;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;
import static utils.Constants.QUIZDB;

@WebServlet("/saveQuestion")
public class SaveQuestionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        JsonObject body = JsonParser.parseReader(request.getReader()).getAsJsonObject();


        String type = body.get("type").getAsString();
        if (QType.SingleChoice.name().equals(type)) {
            handleSingleChoice(out, body,  response);
        }else if(QType.MultiChoice.name().equals(type)){

        }else{

        }




    }

    private void handleSingleChoice(PrintWriter out, JsonObject body, HttpServletResponse response){
        JsonObject jsonResponse = new JsonObject();
        try {
            // Read request body
            QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

            int quizId = body.get("quizId").getAsInt();

            List<Quiz> quizzes = quizDB.query(
                    new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
            );

            // Verify quiz exists first
            if (quizzes.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid quiz ID: " + quizId);
                out.print(jsonResponse);
                return;
            }

            String question = body.get("question").getAsString();
            int correctIndex = body.get("correctIndex").getAsInt();
            String photoUrl = body.has("photoUrl") && !body.get("photoUrl").isJsonNull()
                    ? body.get("photoUrl").getAsString() : null;
            int points = body.has("points") ? body.get("points").getAsInt() : 1;

            JsonArray optionArray = body.getAsJsonArray("options");
            List<String> options = new ArrayList<>();
            for (int i = 0; i < optionArray.size(); i++) {
                options.add(optionArray.get(i).getAsString());
            }

            // Validation
            if (question == null || question.isEmpty() ||
                    options.size() < 2 || correctIndex < 0 || correctIndex >= options.size() ||
                    options.stream().anyMatch(String::isEmpty) || points < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid question format");
            } else {
                QuestionSingleChoice q = new QuestionSingleChoice(quizId, question, correctIndex, options, photoUrl, points);
                questionDB.add(q);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Question saved successfully.");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            response.setContentType("application/json");
            out.print(jsonResponse);
            out.flush();
        }
    }
}


