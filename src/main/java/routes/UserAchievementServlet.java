package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.AchievementField;
import databases.implementations.AchievementDB;
import objects.user.Achievement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.ACHIEVEMENTSDB;

@WebServlet("/achievement")
public class UserAchievementServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));

        AchievementDB achievementDB = (AchievementDB) getServletContext().getAttribute(ACHIEVEMENTSDB);

        List<Achievement> list = achievementDB.query(new FilterCondition<>(AchievementField.ID, Operator.EQUALS, id));
        Achievement achievement = list.get(0);

        Gson gson = new Gson();
        String json = gson.toJson(achievement);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}



