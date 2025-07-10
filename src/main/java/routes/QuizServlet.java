package routes;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpContext;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.QuizResultField;
import databases.implementations.QuizResultDB;
import objects.user.QuizResult;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static utils.Constants.QUIZRESULTDB;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response){

        try {
            request.getRequestDispatcher("/quiz.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response){



    }

}
