package databases.implementations;

import databases.DataBase;
import databases.filters.fields.UserAchievementField;
import objects.user.UserAchievement;
import java.sql.*;
public class UserAchievementDB extends DataBase<UserAchievement, UserAchievementField> {
    public UserAchievementDB(Connection connection){super(connection, UserAchievement.class);}
}
