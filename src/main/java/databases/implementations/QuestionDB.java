package databases.implementations;

import databases.DataBase;
import databases.filters.fields.QuestionField;
import objects.questions.Question;

import java.sql.*;

public class QuestionDB extends DataBase<Question, QuestionField> {
    public QuestionDB(Connection con) {super(con, Question.class);}
}
