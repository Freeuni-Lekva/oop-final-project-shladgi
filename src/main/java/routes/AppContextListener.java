package routes;

import databases.implementations.UserDB;

import javax.naming.Context;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce){
        //TODO
        //initialize necessary items
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quizkhana", "root", "root");
            UserDB userDB = new UserDB(conn);

            ServletContext context = sce.getServletContext();
            context.setAttribute("UserDB", userDB);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }
}
