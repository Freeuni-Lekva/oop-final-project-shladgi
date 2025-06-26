package databases.implementations;

import databases.DataBase;
import databases.filters.fields.FriendshipField;
import objects.user.Friendship;

import java.sql.Connection;

public class FriendshipDB extends DataBase<Friendship, FriendshipField> {
    public FriendshipDB(Connection con) {super(con, Friendship.class);}
}
