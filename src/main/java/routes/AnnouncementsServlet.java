package routes;

import com.google.gson.JsonObject;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.AnnouncementField;
import databases.implementations.AnnouncementDB;
import objects.Announcement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
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

        public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                AnnouncementDB announcementDB = (AnnouncementDB) getServletContext().getAttribute(ANNOUNCEMENTSDB);
                Object o =  request.getSession().getAttribute("type") ;

                JsonObject json = new JsonObject();

                if (o == null || !o.toString().equalsIgnoreCase("Admin")) {
                        json.addProperty("success", false);
                        json.addProperty("message", "Access denied. Only admins can delete announcements.");
                        out.print(json);
                        return;
                }

                String idParam = request.getParameter("id");
                if (idParam == null) {
                        json.addProperty("success", false);
                        json.addProperty("message", "Missing announcement ID.");
                        out.print(json);
                        return;
                }

                int announcementId;
                try {
                        announcementId = Integer.parseInt(idParam);
                } catch (NumberFormatException e) {
                        json.addProperty("success", false);
                        json.addProperty("message", "Invalid announcement ID.");
                        out.print(json);
                        return;
                }

                int deleted = announcementDB.delete(new FilterCondition<>(AnnouncementField.ID, Operator.EQUALS, announcementId));

                if (deleted==1) {
                        json.addProperty("success", true);
                        json.addProperty("message", "Announcement deleted successfully.");
                } else {
                        json.addProperty("success", false);
                        json.addProperty("message", "Announcement not found or deletion failed.");
                }
                out.print(json);

        }
}




