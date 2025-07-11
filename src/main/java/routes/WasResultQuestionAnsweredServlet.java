package routes;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserAnswerField;
import databases.implementations.UserAnswerDB;
import objects.user.UserAnswer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.USERANSWERDB;

@WebServlet("/was-result-question-answered")
public class WasResultQuestionAnsweredServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String questionIdStr = request.getParameter("questionId");
        String resultIdStr = request.getParameter("resultId");

        JsonObject json = new JsonObject();

        if (questionIdStr == null || resultIdStr == null) {
            json.addProperty("success", false);
            json.addProperty("error", "Missing parameters.");
            response.getWriter().write(json.toString());
            return;
        }

        try {
            int questionId = Integer.parseInt(questionIdStr);
            int resultId = Integer.parseInt(resultIdStr);

            UserAnswerDB userAnswerDB = (UserAnswerDB) getServletContext().getAttribute(USERANSWERDB);
            List<UserAnswer> uas = userAnswerDB.query(new FilterCondition<>(UserAnswerField.RESULTID, Operator.EQUALS, resultId),
                    new FilterCondition<>(UserAnswerField.QUESTIONID, Operator.EQUALS, questionId));

            if(uas.isEmpty()) {
                json.addProperty("success", true);
                json.addProperty("answered", false);
            }else{
                Gson gson = new Gson();
                json.addProperty("success", true);
                json.addProperty("answered", true);
                json.add("userAnswer", gson.toJsonTree(uas.getFirst()).getAsJsonObject());
            }
        } catch (NumberFormatException e) {
            json.addProperty("success", false);
            json.addProperty("error", "Invalid number format.");
        }

        response.getWriter().write(json.toString());
    }

}
