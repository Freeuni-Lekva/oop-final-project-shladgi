package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.NoteField;
import databases.filters.fields.UserField;
import databases.implementations.ChallengeDB;
import databases.implementations.NoteDB;
import databases.implementations.UserDB;
import objects.user.Note;
import objects.user.User;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.List;

import static utils.Constants.*;

@WebServlet("/getNotes")
public class getNotesServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse res){
        try {
            int userId = Integer.parseInt(req.getParameter("userid"));
            int interval =Integer.parseInt(req.getParameter("interval"));
            ServletContext context = req.getServletContext();
            NoteDB noteDB = (NoteDB) context.getAttribute(NOTEDB);
            UserDB userDB = (UserDB) context.getAttribute(USERDB);
            List<Note> unseenNotes = noteDB.query((List<FilterCondition<NoteField>>) List.of(
                    new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, userId),
                    new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, false)),
                    NoteField.CREATIONDATE,
                    true,
                    10,
                    0
            );

            List<Note> seenNotes = List.of();
            if(10 > unseenNotes.size() && !unseenNotes.isEmpty()){
                seenNotes = noteDB.query((List<FilterCondition<NoteField>>) List.of(
                                new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, userId),
                                new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, true)),
                        NoteField.CREATIONDATE,
                        true,
                        10-unseenNotes.size(),
                        0
                );

            }
            System.out.println(unseenNotes.size());
            System.out.println(seenNotes.size());

            for(Note note : unseenNotes){
                noteDB.delete(new FilterCondition<>(NoteField.ID, Operator.EQUALS, note.getId()));
                note.setViewed(true);
                noteDB.add(note);
                note.setViewed(false);
            }
            if(unseenNotes.size() ==0){
                seenNotes = noteDB.query((List<FilterCondition<NoteField>>) List.of(
                                new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, userId),
                                new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, true)),
                        NoteField.CREATIONDATE,
                        true,
                        10,
                        (interval -1)*10
                );
            }

            for(Note n : seenNotes){
                unseenNotes.add(n);
            }

            JsonArray ja = new JsonArray();
            for(Note n : unseenNotes){
                User sender = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS,n.getSenderId() )).getFirst();
                JsonObject j = new JsonObject();
                j.addProperty("id", n.getId());
                j.addProperty("viewed", n.isViewed());
                j.addProperty("text", n.getText());
                j.addProperty("createDate", String.valueOf(n.getCreationDate()));
                j.addProperty("senderName", sender.getUserName());
                j.addProperty("senderId", sender.getId());
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
            System.out.println(555);
            System.out.println(e.getMessage());
            System.out.println(555);
        }




    }
}
