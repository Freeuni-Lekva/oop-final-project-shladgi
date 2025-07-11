package databases.implementations;

import databases.DataBase;
import databases.filters.fields.SqlField;
import databases.filters.fields.UserTokenField;
import objects.user.UserToken;

import java.sql.Connection;

public class UserTokenDB extends DataBase<UserToken, UserTokenField> {
    /**
     * Constructor that gets the Connection to the database and the Class for the Field it accepts(for filters)
     *
     * @param con   Connection
     *  clazz SqlField Subclass that corresponds to the Class this database is for
     */
    public UserTokenDB(Connection con) {
        super(con, UserToken.class);
    }


}
