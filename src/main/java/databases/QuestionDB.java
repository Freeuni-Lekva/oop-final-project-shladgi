package databases;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mysql.cj.protocol.Resultset;
import databases.filters.Filter;
import databases.filters.fields.QuestionField;
import objects.questions.QType;
import objects.questions.Question;
import objects.questions.QuestionMaker;
import objects.questions.QuestionSingleChoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDB extends DataBase<Question, QuestionField> {
    public QuestionDB(Connection con) {super(con, Question.class);}
}
