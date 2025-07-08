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
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id = req.getParameter("id");
        String act = req.getParameter("act");
        ServletContext context = req.getServletContext();
        NoteDB noteDB = (NoteDB) context.getAttribute(NOTEDB);
        ChallengeDB challengeDB = (ChallengeDB)  context.getAttribute(CHALLENGEDB);
        List<Note> notes = noteDB.query(new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, id));
        List<Challenge> challenges = challengeDB.query(new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, id));
        JsonObject jo = new JsonObject();


        if(act != null && act.equals("hasUnseen") ){
            res.setContentType("application/json");
            jo.addProperty("hasUnseen", checkViews(notes,challenges));
            res.getWriter().write(jo.toString());
        }



    }





    private boolean checkViews(List<Note> notes,List<Challenge> challenges){
        for(Note note : notes){
            if(!note.isViewed()){
                return true;
            }
        }
       for(Challenge challenge : challenges) {
           if (!challenge.isViewed()) {
               return true;
           }
       }
      return false;
    }


}
