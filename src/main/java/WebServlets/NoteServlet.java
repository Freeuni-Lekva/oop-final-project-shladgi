package WebServlets;

import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.ChallengeDB;
import databases.implementations.NoteDB;
import databases.implementations.UserDB;
import objects.user.Challenge;
import objects.user.Note;
import objects.user.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String friendUsername = request.getParameter("FriendUsername");
        String message = request.getParameter("Message");

        // es loginisas unda chavagdo sesiashi wesit.
        User sender = (User) request.getSession().getAttribute("user");

        if (friendUsername == null || message == null || message.isBlank() || sender == null) {
            response.sendRedirect("challengeNote.html");
            return;
        }

        // wamovige DAO-ebi.
        UserDB userDB = (UserDB) getServletContext().getAttribute("userDB");
        NoteDB noteDB = (NoteDB) getServletContext().getAttribute("noteDB");

        // gadavmowmot friend tu arsebobs
        List<FilterCondition<UserField>> filters = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, friendUsername)
        );
        List<User> result = userDB.query(filters);
        User recipient = result.isEmpty() ? null : result.get(0);

        if(recipient == null ){
            // allert("wrong friend's userName");
            response.sendRedirect("ChallengeNote.html");
            return;
        }else{
            // tu arsebobs axla gadavamowmot megobroba.
            // jer vergavige rogoraa chagdebuli bazashi kargad da mere davwer.
            //TO DO

        }

        Note note = new Note(sender.getId(),
                recipient.getId(),
                LocalDateTime.now(),
                message);

        noteDB.add(note);
        response.sendRedirect("index.html");
    }

}









