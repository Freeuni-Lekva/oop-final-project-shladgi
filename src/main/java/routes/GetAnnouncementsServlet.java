package routes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import databases.implementations.AnnouncementDB;
import objects.Announcement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static utils.Constants.ANNOUNCEMENTSDB;

@WebServlet("/get-announcement")
public class GetAnnouncementsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AnnouncementDB announcementDB = (AnnouncementDB) getServletContext().getAttribute(ANNOUNCEMENTSDB);

        List<Announcement> announcements = announcementDB.getAllAnnouncements();

        JsonArray jsonArray = new JsonArray();
        for (Announcement announcement : announcements) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", announcement.getId());

            jsonObject.addProperty("title", announcement.getTitle());
            jsonObject.addProperty("image", announcement.getImageLink());
            jsonObject.addProperty("content", announcement.getContent());
            jsonObject.addProperty("author", announcement.getAuthor());
            jsonObject.addProperty("date", announcement.getCreationDate().toString());
            jsonArray.add(jsonObject);
        }

        String json = jsonArray.toString(); // ← serialize the built JsonArray
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}
