package databases.implementations;

import databases.DataBase;
import databases.filters.fields.AnnouncementField;
import objects.Announcement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDB extends DataBase<Announcement, AnnouncementField> {
    public AnnouncementDB(Connection con){super(con, Announcement.class);}

    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String sql = """
        SELECT id, imagelink, title, content, author, creationdate
        FROM announcements
        ORDER BY creationdate DESC
    """;

        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Announcement ann = new Announcement(
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("author"),
                        rs.getString("imagelink"),
                        rs.getTimestamp("creationdate").toLocalDateTime());
                announcements.add(ann);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return announcements;
    }
}
