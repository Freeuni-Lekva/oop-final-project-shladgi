package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.implementations.QuestionDB;
import databases.implementations.QuizDB;
import objects.Quiz;
import objects.questions.QType;
import objects.questions.QuestionFillInBlanks;
import objects.questions.QuestionMultiChoice;
import objects.questions.QuestionSingleChoice;
import objects.questions.QuestionTextAnswer;
import objects.questions.QuestionMultiTextAnswer;

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
            handleMultiChoice(out, body, response);
        } else if (QType.FillInBlanks.name().equals(type)) { // New handler for FillInBlanks
            handleFillInBlanks(out, body, response);
        }else if (QType.TextAnswer.name().equals(type)){
            handleTextAnswer(out, body,  response);
        } else if (QType.MultiTextAnswer.name().equals(type)) {
            handleMultiTextAnswer(out, body, response);
        }else{

        }

    }

    private void handleMultiTextAnswer(PrintWriter out, JsonObject body, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();
        try {
            QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

            int quizId = body.get("quizId").getAsInt();

            List<Quiz> quizzes = quizDB.query(
                    new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
            );

            if (quizzes.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid quiz ID: " + quizId);
                out.print(jsonResponse);
                return;
            }

            String question = body.get("question").getAsString();
            boolean exactMatch = body.get("exactMatch").getAsBoolean();
            boolean ordered = body.get("ordered").getAsBoolean();
            String photoUrl = body.has("photoUrl") && !body.get("photoUrl").isJsonNull()
                    ? body.get("photoUrl").getAsString() : null;
            int points = body.has("points") ? body.get("points").getAsInt() : 1;

            JsonArray correctAnswersArray = body.getAsJsonArray("correctAnswers");
            List<List<String>> correctAnswers = new ArrayList<>();
            for (JsonElement answerGroupElement : correctAnswersArray) {
                JsonArray answerGroupArray = answerGroupElement.getAsJsonArray();
                List<String> answerGroup = new ArrayList<>();
                for (JsonElement answerElement : answerGroupArray) {
                    answerGroup.add(answerElement.getAsString());
                }
                correctAnswers.add(answerGroup);
            }

            // Validation
            if (question == null || question.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Question text cannot be empty");
            } else if (correctAnswers.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "At least one answer group is required");
            } else if (correctAnswers.stream().anyMatch(List::isEmpty)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Each answer group must contain at least one answer");
            } else if (correctAnswers.stream().flatMap(List::stream).anyMatch(String::isEmpty)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Answer texts cannot be empty");
            } else if (points < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Points value must be at least 1");
            } else {
                QuestionMultiTextAnswer q = new QuestionMultiTextAnswer(
                        quizId,
                        question,
                        correctAnswers,
                        exactMatch,
                        ordered,
                        photoUrl,
                        points
                );
                questionDB.add(q);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Multi-Text Answer Question saved successfully.");
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

    private void handleFillInBlanks(PrintWriter out, JsonObject body, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();
        try {
            QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

            int quizId = body.get("quizId").getAsInt();

            List<Quiz> quizzes = quizDB.query(
                    new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
            );

            if (quizzes.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid quiz ID: " + quizId);
                out.print(jsonResponse);
                return;
            }

            String question = body.get("question").getAsString();
            JsonArray blankIdxArray = body.getAsJsonArray("blankIdx");

            // amas agar viyenebt mainc
            List<Integer> blankIdx = new ArrayList<>();
            /*for (JsonElement element : blankIdxArray) {
                blankIdx.add(element.getAsInt());
            }*/

            JsonArray correctAnswersArray = body.getAsJsonArray("correctAnswers");
            List<List<String>> correctAnswers = new ArrayList<>();
            for (JsonElement blankAnswersElement : correctAnswersArray) {
                JsonArray possibleAnswersArray = blankAnswersElement.getAsJsonArray();
                List<String> possibleAnswers = new ArrayList<>();
                for (JsonElement answerElement : possibleAnswersArray) {
                    possibleAnswers.add(answerElement.getAsString());
                }
                correctAnswers.add(possibleAnswers);
            }

            boolean exactMatch = body.get("exactMatch").getAsBoolean();
            String photoUrl = body.has("photoUrl") && !body.get("photoUrl").isJsonNull()
                    ? body.get("photoUrl").getAsString() : null;
            int points = body.has("points") ? body.get("points").getAsInt() : 1;

            // Validation specific to FillInBlanks
            if (question == null || question.isEmpty() ||
                    correctAnswers.isEmpty() || // Must have correct answers for blanks
                    correctAnswers.stream().anyMatch(List::isEmpty) || // Each blank must have at least one answer
                    correctAnswers.stream().flatMap(List::stream).anyMatch(String::isEmpty) || // No empty answer strings
                    points < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid Fill-in-the-Blanks question format or missing fields.");
            } else {
                QuestionFillInBlanks q = new QuestionFillInBlanks(quizId, question, blankIdx, correctAnswers, exactMatch, photoUrl, points);
                questionDB.add(q);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Fill-in-the-Blanks Question saved successfully.");
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
    private void handleMultiChoice(PrintWriter out, JsonObject body, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();
        try {
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
            JsonArray correctIndexesArray = body.getAsJsonArray("correctIndexes");
            List<Integer> correctIndexes = new ArrayList<>();
            for (int i = 0; i < correctIndexesArray.size(); i++) {
                correctIndexes.add(correctIndexesArray.get(i).getAsInt());
            }

            String photoUrl = body.has("photoUrl") && !body.get("photoUrl").isJsonNull()
                    ? body.get("photoUrl").getAsString() : null;
            int points = body.has("points") ? body.get("points").getAsInt() : 1;
            boolean exactMatch = body.get("exactMatch").getAsBoolean();

            JsonArray optionArray = body.getAsJsonArray("options");
            List<String> options = new ArrayList<>();
            for (int i = 0; i < optionArray.size(); i++) {
                options.add(optionArray.get(i).getAsString());
            }

            // Validation
            if (question == null || question.isEmpty() ||
                    options.size() < 2 || correctIndexes.isEmpty() ||
                    correctIndexes.stream().anyMatch(idx -> idx < 0 || idx >= options.size()) ||
                    options.stream().anyMatch(String::isEmpty) || points < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid multi-choice question format or missing fields.");
            } else {
                QuestionMultiChoice q = new QuestionMultiChoice(quizId, question, correctIndexes, options, exactMatch, photoUrl, points);
                questionDB.add(q);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Multi-Choice Question saved successfully.");
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

    private void handleTextAnswer(PrintWriter out, JsonObject body, HttpServletResponse response) {
        JsonObject jsonResponse = new JsonObject();

        try {
            QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

            int quizId = body.get("quizid").getAsInt();

            List<Quiz> quizzes = quizDB.query(
                    new FilterCondition<>(QuizField.ID, Operator.EQUALS, quizId)
            );

            // Check if quiz exists
            if (quizzes.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid quiz ID: " + quizId);
                out.print(jsonResponse);
                return;
            }

            // Parse and validate fields
            String question = body.get("question").getAsString();
            String imageLink = body.has("imageLink") && !body.get("imageLink").isJsonNull()
                    ? body.get("imageLink").getAsString()
                    : null;
            int points = body.has("points") ? body.get("points").getAsInt() : 1;
            boolean exactMatch = body.has("exactMatch") && body.get("exactMatch").getAsBoolean();

            JsonArray correctAnswersJson = body.getAsJsonArray("correctAnswers");
            List<String> correctAnswers = new ArrayList<>();
            for (int i = 0; i < correctAnswersJson.size(); i++) {
                String answer = correctAnswersJson.get(i).getAsString().trim();
                if (!answer.isEmpty()) {
                    correctAnswers.add(answer);
                }
            }

            // Validation
            if (question == null || question.isEmpty() ||
                    correctAnswers.isEmpty() ||
                    points < 1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid question format");
            } else {
                QuestionTextAnswer q = new QuestionTextAnswer(
                        quizId,
                        imageLink,
                        question,
                        correctAnswers,
                        exactMatch,
                        points
                );
                questionDB.add(q);
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "TextAnswer question saved successfully.");
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


