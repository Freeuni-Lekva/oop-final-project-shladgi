package objects.questions;


import com.google.gson.JsonObject;

public abstract class Question {
    protected int id;
    protected int quizId;
    protected String question;
    protected String imageLink;
    protected QType type;
    protected int maxScore;

    public Question(){}

    //Question Constructor that will construct the object from database row
    public Question(int id, int quizId, String question, String imageLink, int maxScore, JsonObject json, QType type) {
        this.id = id;
        this.quizId = quizId;
        this.type = type;
        this.question = question;
        this.imageLink = imageLink;
        this.maxScore = maxScore;
        putData(json);
    }

    // checks the answer and returns a score
    public abstract int check(Answer<?> answer);

    // json storing for database
    // generates json from the data that is specific to a given class
    public abstract JsonObject getData();

    // given the json data generated for this object. store it in this object
    // variables
    protected abstract void putData(JsonObject json);




    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public int getQuizId() {return quizId;}

    public void setQuizId(int quizId) {this.quizId = quizId;}

    public String getQuestion() {return question;}

    public void setQuestion(String question) {this.question = question;}

    public String getImageLink() {return imageLink;}

    public void setImageLink(String imageLink) {this.imageLink = imageLink;}

    public QType getType() {return type;}

    public void setType(QType type) {this.type = type;}

    public int getMaxScore() {return maxScore;}

    public void setMaxScore(int maxScore) {this.maxScore = maxScore;}
}