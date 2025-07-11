package databases.implementations;

import databases.DataBase;
import databases.filters.FilterBuilder;
import databases.filters.FilterCondition;
import databases.filters.fields.NoteField;
import objects.user.Note;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

public class NoteDB extends DataBase<Note, NoteField> {
    public NoteDB(Connection connection) {super(connection, Note.class);}

    public void updateViewed(List<FilterCondition<NoteField>> filterConditions, boolean viewed) {
        String filterString = FilterBuilder.buildFilter(filterConditions);
        try (PreparedStatement stmt = con.prepareStatement("UPDATE " + tableName + " SET  viewed = "+ viewed +   " WHERE " + filterString)) {
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("update ERROR \n" + e.getMessage());
        }


    }
}