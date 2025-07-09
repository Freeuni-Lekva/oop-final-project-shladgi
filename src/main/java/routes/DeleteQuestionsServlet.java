package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuestionField;
import databases.implementations.QuestionDB;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static utils.Constants.QUESTIONDB;

@WebServlet("/deleteQuestions")
public class DeleteQuestionsServlet extends HttpServlet {


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        String quizIdStr = request.getParameter("quizid");
        if(quizIdStr == null) return;

        int quizId = Integer.parseInt(quizIdStr);

        QuestionDB questionDB = (QuestionDB) getServletContext().getAttribute(QUESTIONDB);

        questionDB.delete(new FilterCondition<>(QuestionField.QUIZID, Operator.EQUALS, quizId));

    }

}
