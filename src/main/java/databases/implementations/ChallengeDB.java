package databases.implementations;

import databases.DataBase;
import databases.filters.FilterBuilder;
import databases.filters.FilterCondition;
import databases.filters.fields.ChallengeField;
import databases.filters.fields.NoteField;
import objects.user.Challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class ChallengeDB extends DataBase<Challenge, ChallengeField> {
    public ChallengeDB(Connection connection) {super(connection, Challenge.class);}

    public void updateViewed(List<FilterCondition<ChallengeField>> filterConditions, boolean viewed) {
        String filterString = FilterBuilder.buildFilter(filterConditions);
        try (PreparedStatement stmt = con.prepareStatement("UPDATE " + tableName + " SET  viewed = "+ viewed +   " WHERE " + filterString)) {
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("update ERROR \n" + e.getMessage());
        }


    }

}
