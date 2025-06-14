package objects.questions;


import com.google.gson.JsonObject;

public abstract class Question {
    private int id;
    private String question;
    private String imageLink;
    private QType type;
    private int maxScore;

    // checks the answer and returns a score
    public abstract int check(Answer<?> answer);

    // json storing for database
    // generates json from the data that is specific to a given class
    public abstract JsonObject getData();

    // given the json data generated for this object. store it in this object
    // variables
    public abstract void putData(JsonObject json);




    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getQuestion() {return question;}

    public void setQuestion(String question) {this.question = question;}

    public String getImageLink() {return imageLink;}

    public void setImageLink(String imageLink) {this.imageLink = imageLink;}

    public QType getType() {return type;}

    public void setType(QType type) {this.type = type;}

    public int getMaxScore() {return maxScore;}

    public void setMaxScore(int maxScore) {this.maxScore = maxScore;}
}