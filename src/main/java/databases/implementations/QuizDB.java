package databases.implementations;

import databases.DataBase;
import databases.filters.fields.QuizField;
import objects.Quiz;

import java.sql.Connection;

public class QuizDB extends DataBase<Quiz, QuizField> {
    public QuizDB(Connection connection) {super(connection, Quiz.class);}
}
