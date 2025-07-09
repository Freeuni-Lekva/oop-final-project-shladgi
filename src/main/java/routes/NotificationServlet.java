package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.ChallengeField;
import databases.filters.fields.NoteField;
import databases.implementations.ChallengeDB;
import databases.implementations.NoteDB;
import objects.user.Challenge;
import objects.user.Note;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static utils.Constants.CHALLENGEDB;
import static utils.Constants.NOTEDB;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.getRequestDispatcher("/notifications.html").forward(req, res);
    }



    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
       try {
        String id = req.getParameter("id");
        String act = req.getParameter("act");
        ServletContext context = req.getServletContext();
        NoteDB noteDB = (NoteDB) context.getAttribute(NOTEDB);
        ChallengeDB challengeDB = (ChallengeDB)  context.getAttribute(CHALLENGEDB);

        if(act != null && act.equals("hasUnseen") ){
            checkViews(res,noteDB,challengeDB,id);
        }

       } catch (Exception e) {
           JsonObject jo = new JsonObject();
           res.setContentType("application/json");
           jo.addProperty("success", false );
           res.getWriter().write(jo.toString());
       }



    }





    private void checkViews(HttpServletResponse res, NoteDB noteDB, ChallengeDB challengeDB, String id ) throws IOException {
        List<Note> notes = noteDB.query(List.of(
                new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, id),
                new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, false))
        );
        List<Challenge> challenges = challengeDB.query( List.of(
                new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, id),
                new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, false))
        );
        JsonObject jo = new JsonObject();
        res.setContentType("application/json");
        jo.addProperty("success", true );
        System.out.println("notes "+notes.size());
        System.out.println("chal"+challenges.size());
        jo.addProperty("hasUnseen",notes.size()>0 || challenges.size()>0 );
        res.getWriter().write(jo.toString());

    }


}
