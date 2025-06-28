package databases.implementations;

import databases.DataBase;
import databases.filters.fields.FriendRequestField;
import objects.user.FriendRequest;

import java.sql.Connection;

public class FriendRequestDB extends DataBase<FriendRequest, FriendRequestField> {
    public FriendRequestDB(Connection connection) {super(connection, FriendRequest.class);}
}