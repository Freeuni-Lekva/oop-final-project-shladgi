package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "users")
public class User {
    @Column(name = "id", primary = true)
    private int id;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "type")
    private UserType type;

    @Column(name = "creationdate")
    private LocalDateTime creationDate;

    public User(){}

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
