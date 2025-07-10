package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.*;
import databases.implementations.*;
import objects.Quiz;
import objects.user.Friendship;
import objects.user.QuizResult;
import objects.user.User;
import objects.user.UserAchievement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static utils.Constants.*;

@WebServlet("/recent-activities")
public class loadRecentActivitiesServlet extends HttpServlet {

    private static class Activity {
        public JsonObject json;           // holds the JSON object describing the activity
        public String creationTime;       // formatted creation time (for sorting)

        public Activity(JsonObject json, String creationTime) {
            this.json = json;
            this.creationTime = creationTime;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);
        UserAchievementDB userAchievementDB = (UserAchievementDB) getServletContext().getAttribute(USERACHIEVEMENTDB);
        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);

        String username = (String) request.getSession().getAttribute("username");
        if (username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        List<User> users = userDB.query(new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, username));
        if (users.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User user = users.get(0);

        // Find friends
        List<Friendship> friends1 = friendshipDB.query(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, user.getId()));
        List<Friendship> friends2 = friendshipDB.query(new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, user.getId()));

        Set<Integer> friendIds = new HashSet<>();
        for (Friendship f : friends1) friendIds.add(f.getSecondId());
        for (Friendship f : friends2) friendIds.add(f.getFirstId());

        if (friendIds.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("[]");
            return;
        }

        List<Activity> activities = new ArrayList<>();

        for (Integer friendId : friendIds) {
            List<User> friendUsers = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, friendId));
            if (friendUsers.isEmpty()) continue;
            User friend = friendUsers.get(0);

            // Friend's achievements
            List<UserAchievement> friendAchievements = userAchievementDB.query(
                    new FilterCondition<>(UserAchievementField.USERID, Operator.EQUALS, friendId)
            );

            if(!friendAchievements.isEmpty()){
                for (UserAchievement ua : friendAchievements) {
                    JsonObject json = new JsonObject();
                    json.addProperty("type", "achievement_earned");
                    json.addProperty("creationTime", ua.getCreationDate().toString());
                    json.addProperty("friendUsername", friend.getUserName());

                    activities.add(new Activity(json, ua.getCreationDate().toString()));
                }
            }

            // Friend's quizzes
            List<Quiz> friendQuizzes = quizDB.query(new FilterCondition<>(QuizField.USERID, Operator.EQUALS, friendId));
            if(!friendQuizzes.isEmpty()){
                for (Quiz q : friendQuizzes) {
                    JsonObject json = new JsonObject();
                    json.addProperty("type", "quiz_created");
                    json.addProperty("title", "Created quiz: " + q.getTitle());
                    json.addProperty("creationTime",q.getCreationDate().toString());
                    json.addProperty("friendUsername", friend.getUserName());

                    activities.add(new Activity(json, q.getCreationDate().toString()));
                }
            }

            List<QuizResult> quizResults = quizResultDB.query(
                    new FilterCondition<>(QuizResultField.USERID, Operator.EQUALS, friendId),
                    new FilterCondition<>(QuizResultField.TIMETAKEN, Operator.MORE, 0)
            );
            if(!quizResults.isEmpty()){
                for (QuizResult q : quizResults) {
                    JsonObject json = new JsonObject();
                    json.addProperty("type", "quiz_result_earned");
                    json.addProperty("creationTime",q.getCreationDate().toString());
                    json.addProperty("friendUsername", friend.getUserName());
                    json.addProperty("totalScore",  q.getTotalScore());
                    json.addProperty("timeTaken",  q.getTimeTaken());
                    activities.add(new Activity(json, q.getCreationDate().toString()));
                }
            }
        }

        // Sort by creationTime descending
        activities.sort((a, b) -> b.creationTime.compareTo(a.creationTime));

        // Build final JsonArray
        JsonArray jsonArray = new JsonArray();
        for (Activity act : activities) {
            if(jsonArray.size() == 10) break;
            jsonArray.add(act.json);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonArray.toString());
    }
}
