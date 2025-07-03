package databases.implementations;

import databases.DataBase;
import databases.filters.fields.NoteField;
import objects.user.Note;

import java.sql.Connection;

public class NoteDB extends DataBase<Note, NoteField> {
    public NoteDB(Connection connection) {super(connection, Note.class);}
}