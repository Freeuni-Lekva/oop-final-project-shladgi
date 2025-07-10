package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.filters.fields.UserField;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserDB;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

import static utils.Constants.*;

@WebServlet(value= "/admin")
public class AdminPanelServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/admin.html").forward(req, res);
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Object o= req.getSession().getAttribute("type");




        if (o == null || !o.toString().equalsIgnoreCase("Admin")) {
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "Unauthorized access.");
            res.getWriter().write(json.toString());
            return;
        }
        String period = req.getParameter("period");
        if (period == null) period = "all";

        LocalDateTime fromTime = null;
        switch (period) {
            case "24h": fromTime = LocalDateTime.now().minusHours(24); break;
            case "7d":  fromTime = LocalDateTime.now().minusDays(7); break;
            case "30d": fromTime = LocalDateTime.now().minusDays(30); break;
            case "1y": fromTime = LocalDateTime.now().minusDays(365); break;
            default: fromTime = LocalDateTime.of(1990, 1, 1, 0, 0) ;
        }

        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);



        JsonObject json = new JsonObject();
        json.addProperty("totalusers", Integer.valueOf(userDB.query(
                new FilterCondition<>(UserField.ID, Operator.NOTEQ, -1)).size()));
        json.addProperty("newusers", Integer.valueOf(userDB.query(
                new FilterCondition<>(UserField.CREATION_DATE, Operator.MOREEQ, fromTime.toString())).size()));
        json.addProperty("newtakenquizes", Integer.valueOf(quizResultDB.query(
                new FilterCondition<>(QuizResultField.CREATIONDATE, Operator.MOREEQ, fromTime.toString())).size()));
        json.addProperty("totaltakenquizzes", Integer.valueOf(quizResultDB.query(
                new FilterCondition<>(QuizResultField.ID, Operator.NOTEQ, -1)).size()));
        json.addProperty("newquizzes", Integer.valueOf(quizDB.query(
                new FilterCondition<>(QuizField.CREATIONDATE, Operator.MOREEQ, fromTime.toString())).size()));
        json.addProperty("totalquizzes", Integer.valueOf(quizDB.query(
                new FilterCondition<>(QuizField.ID, Operator.NOTEQ, -1)).size()));
        json.addProperty("success", true);
        res.setContentType("application/json");
        res.getWriter().print(json.toString());
    }

}
