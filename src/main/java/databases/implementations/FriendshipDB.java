package databases.implementations;

import databases.DataBase;
import databases.filters.FilterCondition;
import databases.filters.Operator;
import databases.filters.fields.FriendshipField;
import objects.user.Friendship;
import objects.user.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

public class FriendshipDB extends DataBase<Friendship, FriendshipField> {
    public FriendshipDB(Connection con) {super(con, Friendship.class);}

    public boolean areFriends(Integer id1, Integer id2) {
        return !query(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, Math.min(id1, id2)),
                new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, Math.max(id1, id2))).isEmpty();
    }

    public List<Integer> getFriends(int userId) {
        List<Friendship> ret1 = query(new FilterCondition<>(FriendshipField.SECONDID, Operator.EQUALS, userId));
        List<Friendship> ret2 = query(new FilterCondition<>(FriendshipField.FIRSTID, Operator.EQUALS, userId));
        List<Integer> res = new ArrayList<>();

        for(Friendship f : ret1) {
            res.add(f.getFirstId());
        }

        for(Friendship f : ret2) {
            res.add(f.getSecondId());
        }

        return res;
    }
}
