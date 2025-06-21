package databases.filters;

import objects.questions.QType;
import objects.questions.Question;

public class FilterQuestion implements Filter<Question>{
    protected Integer id;
    protected Integer quizId;
    protected QType type;

    // true by default so if everything is null everything gets selected by default  'select * from questions where (true)'
    String queryStr = "(true)";

    /**
     * Static method for easily getting all filter
     * @return new all Filter
     */
    public static FilterQuestion all(){
        return new FilterQuestion(null,null,null);
    }


    /**
     * Construct a Filter for database with given constraints,  'null' means no constraint
     * @param id Question id
     * @param quizId QuizId of the quiz containing this question
     * @param type
     */
    public FilterQuestion(Integer id, Integer quizId, QType type){
        // update the query string
        if(id != null) queryStr+=" AND (id="+id+")";
        if(quizId != null) queryStr+=" AND (quizid="+quizId+")";
        if(type != null) queryStr+="AND (type="+type.name()+")";
    }


    @Override
    public boolean filter(Question x) {
        if(id != null && !id.equals(x.getId())) return false;
        if(quizId != null && !quizId.equals(x.getQuizId())) return false;
        if(type != null && !type.equals(x.getType())) return false;
        return true;
    }

    // this is fot the database query
    @Override
    public String toString(){
        return queryStr;
    }
}
