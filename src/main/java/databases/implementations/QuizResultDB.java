package databases.implementations;

import databases.DataBase;
import databases.filters.fields.QuizResultField;
import objects.user.QuizResult;

import java.sql.Connection;

public class QuizResultDB extends DataBase<QuizResult, QuizResultField> {
    public QuizResultDB(Connection connection) {super(connection, QuizResult.class);}
}