package routes;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuestionField;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuestionDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserAnswerDB;
import objects.questions.Answer;
import objects.questions.Question;
import objects.user.QuizResult;
import objects.user.UserAnswer;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/evalAndSaveUserAnswer")
public class EvalSaveUserAnswerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Parse JSON from request body
        JsonObject body;
        try {
            body = com.google.gson.JsonParser.parseReader(request.getReader()).getAsJsonObject();
        } catch (Exception e) {
            retErr("Invalid JSON input", response);
            return;
        }

        // Extract values from JSON body
        if (!body.has("userId") || !body.has("resultId") || !body.has("questionId")) {
            retErr("Missing required parameters (userId, resultId, questionId)", response);
            return;
        }

        int userId, resultId, questionId;

        try {
            userId = body.get("userId").getAsInt();
            resultId = body.get("resultId").getAsInt();
            questionId = body.get("questionId").getAsInt();
        } catch (Exception e) {
            retErr("userId, resultId, and questionId must be valid integers", response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            retErr("No session found", response);
            return;
        }

        String sessionUserIdStr = (String) session.getAttribute("userid");
        if (sessionUserIdStr == null) {
            retErr("Not logged in", response);
            return;
        }

        int sessionUserIdInt;
        try {
            sessionUserIdInt = Integer.parseInt(sessionUserIdStr);
        } catch (NumberFormatException e) {
            retErr("Invalid user ID in session", response);
            return;
        }

        if (userId != sessionUserIdInt) {
            retErr("Wrong User", response);
            return;
        }

        ServletContext context = getServletContext();
        QuizResultDB quizResultDB = (QuizResultDB) context.getAttribute(QUIZRESULTDB);
        QuestionDB questionDB = (QuestionDB) context.getAttribute(QUESTIONDB);
        UserAnswerDB userAnswerDB = (UserAnswerDB) context.getAttribute(USERANSWERDB);


        List<QuizResult> results = quizResultDB.query(
                new FilterCondition<>(QuizResultField.ID, Operator.EQUALS, resultId),
                new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.LESS, 0),
                new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId)
        );
        if (results.isEmpty()) {
            retErr("Quiz result not found for resultId=" + resultId, response);
            return;
        }

        QuizResult quizResult = results.get(0);
        int quizId = quizResult.getQuizId();

        List<Question> questions = questionDB.query(
                new FilterCondition<>(QuestionField.ID, Operator.EQUALS, questionId),
                new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, quizId)
        );

        if (questions.isEmpty()) {
            retErr("Question not found in this quiz for questionId=" + questionId, response);
            return;
        }
        Question curQuestion = questions.get(0);

        if(!body.has("userAnswer")){
            retErr("No userAnswer found in json", response);
            return;
        }
        JsonObject userAnsJson = body.getAsJsonObject("userAnswer");


        boolean isString = false;
        if (userAnsJson.has("isString")) {
            try {
                isString = userAnsJson.get("isString").getAsBoolean();
            } catch (Exception ignored) {
                retErr("Invalid boolean isString", response);
                return;
            }
        }else{
            retErr("isString is missing", response);
            return;
        }

        JsonArray choices = userAnsJson.getAsJsonArray("choices");

        Answer<?> answer = null;
        if(isString){
            List<String> ls = new ArrayList<>();
            for(int i = 0; i < choices.size(); i++) ls.add(choices.get(i).getAsString());
            answer = new Answer<>(ls);
        }else{
            List<Integer> ls = new ArrayList<>();
            for(int i = 0; i < choices.size(); i++) ls.add(choices.get(i).getAsInt());
            answer = new Answer<>(ls);
        }
        UserAnswer userAns = new UserAnswer(questionId, resultId, isString, answer);


        int correctCnt = curQuestion.check(answer);
        int totalCnt = curQuestion.getMaxScore();
        double points = curQuestion.getWeight() * correctCnt / totalCnt;

        if(quizResult.getTimeTaken() != -2) userAnswerDB.add(userAns);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", true);
        jsonObject.addProperty("message", "Successfully saved and evaluated");
        jsonObject.addProperty("points", points);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(jsonObject);
        out.flush();
    }



    private void retErr(String message, HttpServletResponse response) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);
        jsonObject.addProperty("message", message);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.print(jsonObject);
        out.flush();
    }








}
