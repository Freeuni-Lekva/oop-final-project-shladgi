package routes;

import com.google.gson.JsonObject;
import databases.implementations.QuizDB;
import objects.Quiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

import static utils.Constants.QUIZDB;

@WebServlet("/createQuiz")
public class CreateQuizServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            request.getRequestDispatcher("/createQuizPage.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession();

        if(session == null){
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "User not logged in!");
            response.getWriter().write(json.toString());
            return;
        }

        String userIdStr = session.getAttribute("userid")==null?null:session.getAttribute("userid").toString();


        if(userIdStr == null ||  userIdStr.isEmpty()){
            JsonObject json = new JsonObject();
            json.addProperty("success", false);
            json.addProperty("message", "User not logged in!");
            response.getWriter().write(json.toString());
            return;
        }

        String title = request.getParameter("title");
        String description = request.getParameter("description");
        int random = Integer.parseInt(request.getParameter("random"));
        int singlePage = Integer.parseInt(request.getParameter("singlepage"));
        int immediateCorrection = Integer.parseInt(request.getParameter("immediatecorrection"));
        int practiceMode = Integer.parseInt(request.getParameter("practicemode"));
        int timeLimit = Integer.parseInt(request.getParameter("timelimit"));
        System.out.println("title: " + title);
        System.out.println("desc: " + description);
        System.out.println("random: " + random);
        System.out.println("singlepage: " + singlePage);
        System.out.println("immediate: " + immediateCorrection);
        System.out.println("practivemode: " + practiceMode);
        System.out.println("timelimit: " + timeLimit);

        QuizDB quizDB = (QuizDB) getServletContext().getAttribute(QUIZDB);

        Quiz newQuiz = new Quiz(title, Integer.parseInt(userIdStr), LocalDateTime.now(),
                timeLimit==0?-1:timeLimit, 10,
                12, random==1,
                singlePage==1, immediateCorrection==1,
                practiceMode==1, description);

        quizDB.add(newQuiz);

        JsonObject json = new JsonObject();
        json.addProperty("success", true);
        json.addProperty("quizid", newQuiz.getId());
        response.getWriter().write(json.toString());

    }
}

