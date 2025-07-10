package routes;

import com.google.gson.JsonObject;
import databases.implementations.AnnouncementDB;
import objects.Announcement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import static utils.Constants.*;

@WebServlet("/announcements")
public class AnnouncementsServlet extends HttpServlet {

        public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
                res.setContentType("application/json");
                JsonObject json = new JsonObject();
                Object o =  req.getSession().getAttribute("username");
                String userName = o == null ? "": o.toString();

                Object o2 = req.getSession().getAttribute("type");


                if (o2 == null || !o2.toString().equalsIgnoreCase("Admin")) {
                        json.addProperty("success", false);
                        json.addProperty("message", "Unauthorized.");
                        res.getWriter().write(json.toString());
                        return;
                }

                String title = req.getParameter("title");
                String content = req.getParameter("content");
                String image = req.getParameter("image"); // optional

                if (title == null || content == null || title.isEmpty() || content.isEmpty()) {
                        json.addProperty("success", false);
                        json.addProperty("message", "Title and content are required.");
                        res.getWriter().write(json.toString());
                        return;
                }



                AnnouncementDB announcementDB = (AnnouncementDB) getServletContext().getAttribute(ANNOUNCEMENTSDB);
                Announcement announcement = new Announcement(title, content, userName, image, LocalDateTime.now());
                announcementDB.add(announcement);
                json.addProperty("success", true);
                json.addProperty("message", "Announcement created.");
                res.getWriter().write(json.toString());

        }
        public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        }
        public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        }
}
