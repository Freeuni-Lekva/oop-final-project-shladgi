package databases;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mysql.cj.protocol.Resultset;
import databases.filters.Filter;
import objects.questions.QType;
import objects.questions.Question;
import objects.questions.QuestionMaker;
import objects.questions.QuestionSingleChoice;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDB extends DataBase<Question> {
    Connection con;


    /**
     * Constructor that takes in the Connection to database
     * @param con Connection
     */
    public QuestionDB(Connection con) {
        this.con = con;
    }

    @Override
    public void add(Question entity) {
        try(PreparedStatement stmt = con.prepareStatement("INSERT INTO questions (quizid, imagelink, type, maxscore, jsondata, question) " +
                "Values(?, ?, ?, ?, ?, ?)")){

            // set everything but id
            stmt.setInt(1, entity.getQuizId());
            stmt.setString(2, entity.getImageLink());
            stmt.setString(3, entity.getType().name());
            stmt.setInt(4, entity.getMaxScore());
            stmt.setObject(5, entity.getData().toString(), Types.OTHER);
            stmt.setString(6, entity.getQuestion());


            // add to database
            stmt.executeUpdate();

            // get the generated id and set it to the object
            ResultSet rs =  stmt.getGeneratedKeys();
            if(rs.next()) entity.setId(rs.getInt(1));
            rs.close();

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
    

    @Override
    public int delete(Filter<Question> filter) {
        String filterString = filter.toString();
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM questions WHERE " + filterString)) {
            return stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<Question> query(Filter<Question> filter) {
        List<Question> list = new ArrayList<>();
        String filterString = filter.toString();
        try(PreparedStatement stmt = con.prepareStatement("SELECT * FROM questions WHERE "+filterString)){
            ResultSet rs = stmt.executeQuery();

            // loop over the query result rows and add them to the list
            while(rs.next()){
                list.add(convert(rs));
            }

            rs.close();
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
        return list;
    }


    // Converts The current resultset to a correct question object and returns it
    private Question convert(ResultSet rs){
        Question ret = null;
        try {
            // read columns
            Integer id = rs.getInt("questionid");
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
            throw new RuntimeException("ERROR IN CONVERT FUNCTION IN DATABASE");
        }
        return ret;
    }

}
