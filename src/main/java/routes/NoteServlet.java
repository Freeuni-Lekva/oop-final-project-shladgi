package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.UserField;
import databases.implementations.NoteDB;
import databases.implementations.UserDB;
import databases.implementations.FriendshipDB;
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
import static utils.Constants.*;

@WebServlet("/createNote")
public class NoteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JsonObject json = new JsonObject();

        // DBs
        UserDB userDB = (UserDB) getServletContext().getAttribute(USERDB);
        NoteDB noteDB = (NoteDB) getServletContext().getAttribute(NOTEDB);
        FriendshipDB friendshipDB = (FriendshipDB) getServletContext().getAttribute(FRIENDSHIPDB);

        String friendUsername = request.getParameter("FriendUsername");
        String message = request.getParameter("Message");

        Integer userId = (Integer) request.getSession().getAttribute("userid");
        if (userId == null) {
            json.addProperty("success", false);
            json.addProperty("message", "Not logged in.");
            response.getWriter().write(json.toString());
            return;
        }


        List<User> curUser = userDB.query(new FilterCondition<>(UserField.ID, Operator.EQUALS, userId));
        //es arasdros ar unda moxdes rom sesiidan araswori ID amovige
        if(curUser.isEmpty()){
            json.addProperty("success", false);
            json.addProperty("message", "User not found in database.");
            response.getWriter().write(json.toString());
            return;
        }
        User sender = curUser.get(0);;


        if (friendUsername == null || message == null || message.isBlank()) {
            json.addProperty("success", false);
            json.addProperty("message", "Missing data.");
            response.getWriter().write(json.toString());
            return;
        }



        List<FilterCondition<UserField>> filters = List.of(
                new FilterCondition<>(UserField.USERNAME, Operator.EQUALS, friendUsername)
        );
        List<User> result = userDB.query(filters);
        User recipient = result.isEmpty() ? null : result.get(0);

        if (recipient == null) {
            json.addProperty("success", false);
            json.addProperty("message", "User not found.");
            response.getWriter().write(json.toString());
            return;
        }

        if (!friendshipDB.areFriends(sender.getId(), recipient.getId())) {
            json.addProperty("success", false);
            json.addProperty("message", "You are not friends with this user.");
            response.getWriter().write(json.toString());
            return;
        }

        Note note = new Note(sender.getId(), recipient.getId(), LocalDateTime.now(), message);
        noteDB.add(note);

        json.addProperty("success", true);
        json.addProperty("message", "Note sent successfully.");
        response.getWriter().write(json.toString());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.getRequestDispatcher("createNote.html").forward(request, response);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
