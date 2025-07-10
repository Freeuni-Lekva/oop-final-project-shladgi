package objects.questions;


import com.google.gson.JsonObject;
import databases.annotations.Column;
import databases.annotations.HasJson;
import databases.annotations.Table;
import objects.ObjectWithJson;

@Table(name = "questions")
@HasJson(name = "jsondata")
public abstract class Question implements ObjectWithJson {

    @Column(name = "id", primary = true)
    protected int id;

    @Column(name = "quizid")
    protected int quizId;

    @Column(name = "question")
    protected String question;

    @Column(name = "imagelink")
    protected String imageLink;

    @Column(name = "type")
    protected QType type;

    @Column(name = "maxscore")
    protected int maxScore;

    // this is the score the user gets in a quiz if they get the question correct
    @Column(name = "weight")
    protected double weight;

    public Question(){}

    //Question Constructor that will construct the object from database row
    public Question(int id, int quizId, String question, String imageLink, int maxScore, double weight, JsonObject json, QType type) {
        this.id = id;
        this.quizId = quizId;
        this.type = type;
        this.question = question;
        this.imageLink = imageLink;
        this.maxScore = maxScore;
        this.weight = weight;
        putData(json);
    }

    // checks the answer and returns a score
    public abstract int check(Answer<?> answer);

    public abstract void hideAnswers();

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

    public double getWeight() {return weight;}

    public void setWeight(double weight) {this.weight = weight;}
}