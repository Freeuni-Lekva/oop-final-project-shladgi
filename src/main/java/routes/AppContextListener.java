package routes;

import databases.implementations.*;
import objects.user.UserToken;

import javax.naming.Context;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static utils.Constants.*;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce){
        //TODO
        //initialize necessary items
        try {

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizKhana", "root", "2005TiriTiri" );



            ServletContext context = sce.getServletContext();
            context.setAttribute(USERACHIEVEMENTDB, new UserAchievementDB(conn));
            context.setAttribute(ACHIEVEMENTSDB, new AchievementDB(conn));
            context.setAttribute(USERDB, new UserDB(conn));
            context.setAttribute(CHALLENGEDB, new ChallengeDB(conn));
            context.setAttribute(NOTEDB, new NoteDB(conn));
            context.setAttribute(QUIZDB, new QuizDB(conn));
            context.setAttribute(QUIZRESULTDB, new QuizResultDB(conn));
            context.setAttribute(FRIENDSHIPDB, new FriendshipDB(conn));
            context.setAttribute(USERANSWERDB, new UserAnswerDB(conn));
            context.setAttribute(QUESTIONDB, new  QuestionDB(conn));
            context.setAttribute(FRIENDREQUESTDB, new FriendRequestDB(conn));
            context.setAttribute(USERTOKENDB, new UserTokenDB(conn));
            context.setAttribute(ANNOUNCEMENTSDB, new AnnouncementDB(conn));



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }
}
