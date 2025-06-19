package objects.questions.user;

public class User {
    private int id;
    private String userName;
    private String password;
    private UserType type;


    public User(int id, String userName, String password, UserType type) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.type = type;
    }

    public User(String userName, String password,  UserType type) {
        this.userName = userName;
        this.password = password;
        this.type = type;
    }

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public UserType getType() {return type;}
}
