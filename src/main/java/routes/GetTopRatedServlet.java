package routes;

import com.google.gson.Gson;
import databases.implementations.QuizResultDB;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static utils.Constants.QUIZRESULTDB;

@WebServlet("/get-top-rated-people")
public class GetTopRatedServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        QuizResultDB quizResultDB = (QuizResultDB) getServletContext().getAttribute(QUIZRESULTDB);
        List<String> list = quizResultDB.getTop10People();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }
}
