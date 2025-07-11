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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/achievements")
public class AchievementsServlet extends HttpServlet {


    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){
        String action = request.getParameter("action");

        Integer userId = (Integer) request.getSession().getAttribute("userid");


        if(userId == null){
            throw new RuntimeException("User not logged in!");
        }
        UserAchievementDB userAchievementDB = (UserAchievementDB) getServletContext().getAttribute(USERACHIEVEMENTDB);

        if(action.equals("take")){
            QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
            List<QuizResult> quizResults = quizResultDB.query(new FilterCondition<>( QuizResultField.USERID, Operator.EQUALS, userId));


            int size = quizResults.size();

            UserAchievement userAchievement;

            switch (size) {
                case 1:
                    userAchievement = new UserAchievement(userId, 7, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                case 5:
                    userAchievement = new UserAchievement(userId, 8, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                case 10:
                    userAchievement = new UserAchievement(userId, 4, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                case 50:
                    userAchievement = new UserAchievement(userId, 9, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                case 100:
                    userAchievement = new UserAchievement(userId, 10, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;


                default:
                    break;
            }

            List<QuizResult> maxResults = quizResultDB.query(List.of(new FilterCondition<>( QuizResultField.ID, Operator.MORE, 0)),
                                                                QuizResultField.TOTALSCORE, false, 1, 0);

            if(!maxResults.isEmpty() && maxResults.getFirst().getUserId() == userId){
                UserAchievement userAchievementMax = new UserAchievement(userId, 5, LocalDateTime.now());
                userAchievementDB.add(userAchievementMax);
            }


        }else if(action.equals("create")){
            QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

            List<Quiz> quizzes = quizDB.query(new FilterCondition<>(QuizField.USERID, Operator.EQUALS, userId));

            int size = quizzes.size();
            UserAchievement userAchievement;
            switch (size) {
                case 1:
                    userAchievement = new UserAchievement(userId, 1, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                case 5:
                    userAchievement = new UserAchievement(userId, 2, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                case 10:
                    userAchievement = new UserAchievement(userId, 3, LocalDateTime.now());
                    userAchievementDB.add(userAchievement);
                    break;
                default:
                    break;
            }


        }


    }



}



