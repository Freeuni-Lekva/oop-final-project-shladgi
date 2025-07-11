package databases.implementations;

import databases.DataBase;
import databases.filters.FilterBuilder;
import databases.filters.FilterCondition;
import databases.filters.fields.UserField;
import objects.user.User;
import objects.user.UserType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class UserDB extends DataBase<User, UserField> {
    public UserDB(Connection con ) {super(con, User.class);}
    public void updateType(List<FilterCondition<UserField>> filterConditions, UserType type) {
        String filterString = FilterBuilder.buildFilter(filterConditions);
        try (PreparedStatement stmt = con.prepareStatement("UPDATE " + tableName + " SET  type = '"+ type +   "' WHERE " + filterString)) {
             stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("update ERROR \n" + e.getMessage());
        }


    }
}
