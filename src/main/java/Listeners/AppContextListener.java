package Listeners;

import databases.implementations.*;
import databases.DataBase;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;

@WebListener
public class AppContextListener implements ServletContextListener {
    private Connection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // es ver vqeni da mere gavasworeb.
            /*DataBase database = new DataBase(); // Your custom wrapper
            connection = database.getConnection();*/

            // Store the connection and initialized DBs
            sce.getServletContext().setAttribute("connection", connection);
            sce.getServletContext().setAttribute("userDB", new UserDB(connection));
            sce.getServletContext().setAttribute("challengeDB", new ChallengeDB(connection));
            sce.getServletContext().setAttribute("noteDB", new NoteDB(connection));
            sce.getServletContext().setAttribute("quizDB", new QuizDB(connection));
            sce.getServletContext().setAttribute("quizResultDB", new QuizResultDB(connection));
            sce.getServletContext().setAttribute("friendshipDB", new FriendshipDB(connection));

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /*try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}







































