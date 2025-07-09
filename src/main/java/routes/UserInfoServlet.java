package routes;

import com.google.gson.Gson;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.AchievementField;
import databases.filters.fields.UserAchievementField;
import databases.filters.fields.UserField;
import databases.implementations.AchievementDB;
import databases.implementations.UserAchievementDB;
import databases.implementations.UserDB;
import objects.user.Achievement;
import objects.user.AchievementRarity;
import objects.user.User;
import objects.user.UserAchievement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/user")
public class UserInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.getRequestDispatcher("/user.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserAchievementDB userAchievementDB = (UserAchievementDB) getServletContext().getAttribute(USERACHIEVEMENTDB);
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        AchievementDB achievementDB = (AchievementDB) getServletContext().getAttribute(ACHIEVEMENTSDB);
        String username = request.getParameter("username");

        List<User> users = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));

        if (users.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User u = users.get(0);
        int userId = u.getId();

        List<UserAchievement> userAchievements = userAchievementDB.query(new FilterCondition<>(UserAchievementField.USERID, Operator.EQUALS, userId));

        List<Integer> achievementIds = new ArrayList<>();
        for (UserAchievement us : userAchievements) {
            achievementIds.add(us.getAchievementId());
        }

        List<Achievement> achievements = new ArrayList<>();
        for (Integer id : achievementIds) {
            List<Achievement> achievementstmp = achievementDB.query(new FilterCondition<>(AchievementField.ID, Operator.EQUALS, id));
            achievements.addAll(achievementstmp);
        }

        Gson gson = new Gson();
        String json = gson.toJson(achievements);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
