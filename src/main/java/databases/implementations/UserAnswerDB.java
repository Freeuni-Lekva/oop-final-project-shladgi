package databases.implementations;

import databases.DataBase;
import databases.filters.fields.UserAnswerField;
import objects.user.UserAnswer;

import java.sql.Connection;

public class UserAnswerDB extends DataBase<UserAnswer, UserAnswerField> {
    public UserAnswerDB(Connection connection) {
        super(connection, UserAnswer.class);
    }
}

