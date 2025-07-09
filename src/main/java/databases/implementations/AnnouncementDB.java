package databases.implementations;

import databases.DataBase;
import databases.filters.fields.AnnouncementField;
import objects.Announcement;

import java.sql.Connection;

public class AnnouncementDB extends DataBase<Announcement, AnnouncementField> {

    public AnnouncementDB(Connection con){super(con, Announcement.class);}

}
