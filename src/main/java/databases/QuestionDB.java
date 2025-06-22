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


    public QuestionDB(Connection con) {super(con);}


    @Override
    protected PreparedStatement prepareAddStatement(Question entity) {
        try{
            PreparedStatement stmt = con.prepareStatement(("INSERT INTO questions (quizid, imagelink, type, maxscore, jsondata, question) " +
                    "Values(?, ?, ?, ?, ?, ?)"), Statement.RETURN_GENERATED_KEYS);
            // set everything but id
            stmt.setInt(1, entity.getQuizId());
            stmt.setString(2, entity.getImageLink());
            stmt.setString(3, entity.getType().name());
            stmt.setInt(4, entity.getMaxScore());
            stmt.setString(5, entity.getData().toString());
            stmt.setString(6, entity.getQuestion());
            return stmt;
        }catch (Exception e){
            throw new RuntimeException("ERROR IN PREPARED STATEMENT " + e.getMessage());
        }
    }

    @Override
    protected void setIdWithResultSet(Question entity, ResultSet rs){
        try {
            entity.setId(rs.getInt("id"));
        } catch (SQLException e) {
            throw new RuntimeException("ERROR IN SET ID FUNCTION IN DATABASE " + e.getMessage());
        }
    }

    // Converts The current resultset to a correct question object and returns it
    protected Question convert(ResultSet rs){
        Question ret = null;
        try {
            // read columns
            Integer id = rs.getInt("id");
            Integer quizId = rs.getInt("quizid");
            String question = rs.getString("question");
            String imageLink = rs.getString("imagelink");
            String type = rs.getString("type");
            int maxScore = rs.getInt("maxscore");

            // read json
            String jsonData = rs.getString("jsondata");
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonData, JsonObject.class);


            QType qtype = QType.valueOf(type);

            // make the question
            ret =  QuestionMaker.makeQuestion(id, quizId, question, imageLink, maxScore, qtype, json);

        } catch (SQLException e) {
            throw new RuntimeException("ERROR IN CONVERT FUNCTION IN DATABASE " + e.getMessage());
        }
        return ret;
    }

}
