package databases.implementations;

import databases.DataBase;
import databases.filters.fields.AchievementField;
import objects.user.Achievement;

import java.sql.Connection;

public class AchievementDB extends DataBase<Achievement, AchievementField> {
    public AchievementDB(Connection connection) {super(connection, Achievement.class);}
}