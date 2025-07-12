package routes;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizField;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuizDB;
import databases.implementations.QuizResultDB;
import databases.implementations.UserAchievementDB;
import objects.Quiz;
import objects.user.QuizResult;
import objects.user.UserAchievement;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/achievements")
public class AchievementsServlet extends HttpServlet {


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        Integer userId = (Integer) request.getSession().getAttribute("userid");

        if(userId == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"User not logged in!\"}");
            return;
        }

        UserAchievementDB userAchievementDB = (UserAchievementDB) getServletContext().getAttribute(USERACHIEVEMENTDB);

        boolean achievementAdded = false;
        int addedAchievementId = -1;

        if(action.equals("take")){
            QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
            List<QuizResult> quizResults = quizResultDB.query(new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, userId));

            int size = quizResults.size();

            UserAchievement userAchievement = null;

            switch (size) {
                case 1:
                    userAchievement = new UserAchievement(userId, 7, LocalDateTime.now());
                    break;
                case 5:
                    userAchievement = new UserAchievement(userId, 8, LocalDateTime.now());
                    break;
                case 10:
                    userAchievement = new UserAchievement(userId, 4, LocalDateTime.now());
                    break;
                case 50:
                    userAchievement = new UserAchievement(userId, 9, LocalDateTime.now());
                    break;
                case 100:
                    userAchievement = new UserAchievement(userId, 10, LocalDateTime.now());
                    break;
            }

            if(userAchievement != null){
                userAchievementDB.add(userAchievement);
                achievementAdded = true;
                addedAchievementId = userAchievement.getAchievementId();
            }

            // Check if user has max score on any quiz
            List<QuizResult> maxResults = quizResultDB.query(List.of(new FilterCondition<>(QuizResultField.ID, Operator.MORE, 0)),
                    QuizResultField.TOTALSCORE, false, 1, 0);

            if(!maxResults.isEmpty() && maxResults.get(0).getUserId() == userId){
                UserAchievement userAchievementMax = new UserAchievement(userId, 5, LocalDateTime.now());
                userAchievementDB.add(userAchievementMax);
                achievementAdded = true;
                addedAchievementId = 5; // max score achievement id
            }

        } else if(action.equals("create")){
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
            List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.USERID, Operator.EQUALS, userId));
            int size = quizzes.size();

            UserAchievement userAchievement = null;
            switch (size) {
                case 1:
                    userAchievement = new UserAchievement(userId, 1, LocalDateTime.now());
                    break;
                case 5:
                    userAchievement = new UserAchievement(userId, 2, LocalDateTime.now());
                    break;
                case 10:
                    userAchievement = new UserAchievement(userId, 3, LocalDateTime.now());
                    break;
            }
            if(userAchievement != null){
                userAchievementDB.add(userAchievement);
                achievementAdded = true;
                addedAchievementId = userAchievement.getAchievementId();
            }
        }

        String jsonResponse;
        if(achievementAdded){
            jsonResponse = String.format("{\"success\":true,\"awarded\":%d}", addedAchievementId);
        } else {
            jsonResponse = "{\"success\":false,\"message\":\"No achievements awarded\"}";
        }

        try {
            response.getWriter().write(jsonResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}



