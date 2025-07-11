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

@WebServlet("/getNotifications")
public class GetNotificationsServlet extends HttpServlet {
private int PAGE_SIZE =10;
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("application/json");
        JsonObject rj = new JsonObject();
        try {
            System.out.println();
            Params p = new Params();
            p.userid = (int) req.getSession().getAttribute("userid");
            p.type = req.getParameter("type");
            p.page = Integer.parseInt(req.getParameter("page"));
            p.offset = (p.page - 1) * PAGE_SIZE;

            ServletContext ctx = req.getServletContext();
            p.noteDB = (NoteDB) ctx.getAttribute(NOTEDB);
            p.chalDB = (ChallengeDB) ctx.getAttribute(CHALLENGEDB);
            p.quizDB = (QuizDB) ctx.getAttribute(QUIZDB);
            p.userDB = (UserDB) ctx.getAttribute(USERDB);



            if (p.type.equals("receivedNotes")) {
                handleReceivedNotes(p,rj);
            } else if (p.type.equals("sentNotes")) {
                handleSentNotes(p,rj);
            } else if (p.type.equals("receivedChallenges")) {
                handleReceivedChallenges(p,rj);
            } else if (p.type.equals("sentChallenges")) {
                handleSentChallenges(p,rj);
            } else {
                rj.addProperty("success", false);
                rj.addProperty("message", "Invalid type");
                PrintWriter out = res.getWriter();
                out.write(rj.toString());
                return;
            }
        } catch (Exception e) {
            rj.addProperty("success", false);
            rj.addProperty("message", e.getMessage());
        }

        try (PrintWriter out = res.getWriter()) {
            out.write(rj.toString());
        } catch (Exception ignored) {}
    }




    private void handleReceivedChallenges(Params p, JsonObject jo) throws ServletException, IOException {
        JsonArray infoArray = new JsonArray();

        List<Challenge> unseenChals = p.chalDB.query((List<FilterCondition<ChallengeField>>) List.of(
                        new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, p.userid),
                        new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, false)),
                ChallengeField.CREATIONDATE,
                true,
                PAGE_SIZE+1,
                0
        );
        List<Challenge> seenChals = List.of();
        if(PAGE_SIZE+1 > unseenChals.size() && !unseenChals.isEmpty()){
            seenChals = p.chalDB.query((List<FilterCondition<ChallengeField>>) List.of(
                            new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, p.userid),
                            new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, true)),
                    ChallengeField.CREATIONDATE,
                    true,
                    PAGE_SIZE+1-unseenChals.size(),
                    0
            );

        }

        for(Challenge c : unseenChals){
            p.chalDB.delete(new FilterCondition<>(ChallengeField.ID, Operator.EQUALS, c.getId()));
            c.setViewed(true);
            p.chalDB.add(c);
            c.setViewed(false);
        }
        if(unseenChals.size() ==0){
            seenChals = p.chalDB.query((List<FilterCondition<ChallengeField>>) List.of(
                            new FilterCondition<>(ChallengeField.RECIPIENTID, Operator.EQUALS, p.userid),
                            new FilterCondition<>(ChallengeField.VIEWED, Operator.EQUALS, true)),
                    ChallengeField.CREATIONDATE,
                    true,
                    PAGE_SIZE+1,
                    p.offset
            );
        }
        for(Challenge c : seenChals){
            unseenChals.add(c);
        }

        int n = Math.min(unseenChals.size(), PAGE_SIZE);

        for (int i = 0; i < n; i++) {
            Challenge ch = unseenChals.get(i);
            User sender = p.userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, ch.getSenderId())).get(0);
            Quiz quiz = p.quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, ch.getQuizId())).get(0);
            infoArray.add(getChallengeJson(quiz,sender,ch));
        }
        jo.addProperty("success", true);
        jo.add("items", infoArray);
        jo.addProperty("hasMore", PAGE_SIZE<unseenChals.size());
    }






    private void handleReceivedNotes(Params p, JsonObject jo) throws ServletException, IOException {
        JsonArray infoArray = new JsonArray();
        List<Note> unseenNotes = p.noteDB.query((List<FilterCondition<NoteField>>) List.of(
                        new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, p.userid),
                        new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, false)),
                NoteField.CREATIONDATE,
                true,
                PAGE_SIZE+1,
                0
        );

        List<Note> seenNotes = List.of();
        if(PAGE_SIZE+1 > unseenNotes.size() && !unseenNotes.isEmpty()){
            seenNotes = p.noteDB.query((List<FilterCondition<NoteField>>) List.of(
                            new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, p.userid),
                            new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, true)),
                    NoteField.CREATIONDATE,
                    true,
                    PAGE_SIZE+1-unseenNotes.size(),
                    0
            );

        }

        for(Note note : unseenNotes){
            p.noteDB.updateViewed( List.of(new FilterCondition<>(NoteField.ID, Operator.EQUALS, note.getId())), true );
        }
        if(unseenNotes.size() ==0){
            seenNotes = p.noteDB.query((List<FilterCondition<NoteField>>) List.of(
                            new FilterCondition<>(NoteField.RECIPIENTID, Operator.EQUALS, p.userid),
                            new FilterCondition<>(NoteField.VIEWED, Operator.EQUALS, true)),
                    NoteField.CREATIONDATE,
                    true,
                    PAGE_SIZE+1,
                    p.offset
            );
        }

        for(Note n : seenNotes){
            unseenNotes.add(n);
        }

        int n = Math.min(unseenNotes.size(), PAGE_SIZE);
        for(int i=0;i<n;i++) {
            Note note = unseenNotes.get(i);
            User sender = p.userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, note.getSenderId())).get(0);
            infoArray.add(getNotesJson(note, sender));
        }

        jo.addProperty("success", true);
        jo.add("items", infoArray);
        jo.addProperty("hasMore",  PAGE_SIZE< unseenNotes.size());
    }






    private void handleSentNotes(Params p, JsonObject jo) throws ServletException, IOException {
        JsonArray infoArray = new JsonArray();

        List<Note> notes = p.noteDB.query(List.of(
                new FilterCondition<>(NoteField.SENDERID, Operator.EQUALS, p.userid)
        ), NoteField.CREATIONDATE, true, PAGE_SIZE+1, p.offset);
        int n = Math.min(notes.size(), PAGE_SIZE);
        for(int i=0;i<n;i++) {
            Note note = notes.get(i);
            User recipient = p.userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, note.getFriendId())).get(0);
            infoArray.add(getNotesJson(note,recipient));
        }
        jo.addProperty("success", true);
        jo.add("items", infoArray);
        jo.addProperty("hasMore",  PAGE_SIZE< notes.size());
    }


    private void handleSentChallenges(Params p, JsonObject jo) throws ServletException, IOException {
        JsonArray infoArray = new JsonArray();
        List<Challenge> challenges = p.chalDB.query(List.of(
                new FilterCondition<>(ChallengeField.SENDERID, Operator.EQUALS, p.userid)
        ), ChallengeField.CREATIONDATE, true, PAGE_SIZE+1, p.offset);
        int n = Math.min(challenges.size(), PAGE_SIZE);
        for(int i=0;i<n;i++) {
            Challenge ch = challenges.get(i);
            User recipient = p.userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, ch.getRecipiantId())).get(0);
            Quiz quiz = p.quizDB.query(new FilterCondition<>(QuizField.ID, Operator.EQUALS, ch.getQuizId())).get(0);
            infoArray.add(getChallengeJson(quiz,recipient,ch));
        }
        jo.addProperty("success", true);
        jo.add("items", infoArray);
        jo.addProperty("hasMore", PAGE_SIZE< challenges.size());
    }

    private JsonObject getNotesJson(Note note, User recipient) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", note.getId());
        obj.addProperty("type", "note");
        obj.addProperty("viewed", note.isViewed());
        obj.addProperty("text", note.getText());
        obj.addProperty("createDate", note.getCreationDate().toString());
        obj.addProperty("username", recipient.getUserName());
        obj.addProperty("userid", recipient.getId());
        return obj;
    }

    private JsonObject getChallengeJson(Quiz quiz, User recipient,Challenge ch) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", ch.getId());
        obj.addProperty("type", "challenge");
        obj.addProperty("viewed", ch.isViewed());
        obj.addProperty("createDate", ch.getCreationDate().toString());
        obj.addProperty("username", recipient.getUserName());
        obj.addProperty("userid", recipient.getId());
        obj.addProperty("quizId", quiz.getId());
        obj.addProperty("quizTitle", quiz.getTitle());
        obj.addProperty("score", ch.getBestScore());
        return obj;
    }


    private class Params{
        public int userid;
        public String type;
        public int page;
        public int offset;
        public NoteDB noteDB;
        public ChallengeDB chalDB;
        public QuizDB quizDB;
        public UserDB userDB;
    }




}



