package databases.implementations;

import databases.DataBase;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendshipField;
import objects.user.Friendship;
import objects.user.User;

import java.sql.Connection;
import java.util.ArrayList;

public class FriendshipDB extends DataBase<Friendship, FriendshipField> {
    public FriendshipDB(Connection con) {super(con, Friendship.class);}

    public boolean areFriends(Integer id1, Integer id2) {
        return !query(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, Math.min(id1, id2)),
                new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, Math.max(id1, id2))).isEmpty();
    }
}
