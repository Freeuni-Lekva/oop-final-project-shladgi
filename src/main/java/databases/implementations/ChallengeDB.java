package databases.implementations;

import databases.DataBase;
import databases.filters.fields.ChallengeField;
import objects.user.Challenge;

import java.sql.Connection;

public class ChallengeDB extends DataBase<Challenge, ChallengeField> {
    public ChallengeDB(Connection connection) {super(connection, Challenge.class);}
}
