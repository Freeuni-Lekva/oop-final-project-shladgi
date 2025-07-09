package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.ChallengeField;
import databases.filters.fields.NoteField;
import databases.filters.fields.QuizField;
import databases.filters.fields.UserField;
import databases.implementations.ChallengeDB;
import databases.implementations.NoteDB;
import databases.implementations.QuizDB;
import databases.implementations.UserDB;
import objects.Quiz;
import objects.user.Challenge;
import objects.user.Note;
import objects.user.User;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/getChallanges")
public class getChallengesServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {

            int userId = Integer.parseInt(req.getParameter("userid"));
            int interval =Integer.parseInt(req.getParameter("interval"));
            ServletContext context = req.getServletContext();
            ChallengeDB chalDB = (ChallengeDB) context.getAttribute(CHALLENGEDB);
            UserDB userDB = (UserDB) context.getAttribute(USERDB);
            QuizDB quizDB = (QuizDB) context.getAttribute(QUIZDB);
            List<Challenge> unseenChals = chalDB.query((List<FilterCondition<ChallengeField>>) List.of(
                            new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, userId),
                            new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, false)),
                    ChallengeField.CREATIONDATE,
                    true,
                    10,
                    0
            );
            List<Challenge> seenChals = List.of();
            if(10 > unseenChals.size() && !unseenChals.isEmpty()){
                seenChals = chalDB.query((List<FilterCondition<ChallengeField>>) List.of(
                                new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, userId),
                                new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, true)),
                        ChallengeField.CREATIONDATE,
                        true,
                        10-unseenChals.size(),
                        0
                );

            }

            for(Challenge c : unseenChals){
                chalDB.delete(new FilterCondition<>(ChallengeField.ID, Operator.EQUALS, c.getId()));
                c.setViewed(true);
                chalDB.add(c);
                c.setViewed(false);
            }
            if(unseenChals.size() ==0){
                seenChals = chalDB.query((List<FilterCondition<ChallengeField>>) List.of(
                                new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, userId),
                                new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, true)),
                        ChallengeField.CREATIONDATE,
                        true,
                        10,
                        (interval -1)*10
                );
            }
            for(Challenge c : seenChals){
                unseenChals.add(c);
            }
            JsonArray ja = new JsonArray();
            for(Challenge n : unseenChals){
                User sender = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS,n.getSenderId() )).getFirst();
                Quiz quiz = quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, n.getQuizId())).getFirst();
                JsonObject j = new JsonObject();
                j.addProperty("id", n.getId());
                j.addProperty("viewed", n.isViewed());
                j.addProperty("createDate", String.valueOf(n.getCreationDate()));
                j.addProperty("senderName", sender.getUserName());
                j.addProperty("senderId", sender.getId());
                j.addProperty("quizId", quiz.getId());
                j.addProperty("quizTitle", quiz.getTitle());
                j.addProperty("score",n.getBestScore());
                ja.add(j);
            }

            JsonObject jo = new JsonObject();
            res.setContentType("application/json");
            jo.addProperty("success", true );
            jo.add("info", ja);
            PrintWriter out = res.getWriter();
            out.write(jo.toString());
            out.flush();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
