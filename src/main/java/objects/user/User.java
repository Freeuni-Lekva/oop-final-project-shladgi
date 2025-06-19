package objects.user;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String userName;
    private String password;
    private UserType type;
    private final LocalDateTime creationDate;

    public User(int id, String userName, String password, UserType type, LocalDateTime creationDate) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.type = type;
        this.creationDate = creationDate;
    }

    public User(String userName, String password, UserType type, LocalDateTime creationDate) {
        this.userName = userName;
        this.password = password;
        this.type = type;
        this.creationDate = creationDate;
    }

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public UserType getType() {return type;}
    public LocalDateTime getCreationDate() {return creationDate;}
}
