package databases.implementations;

import databases.DataBase;
import databases.filters.fields.UserField;
import objects.user.User;

import java.sql.Connection;

public class UserDB extends DataBase<User, UserField> {
    public UserDB(Connection con ) {
        super(con, User.class);
    }
}
