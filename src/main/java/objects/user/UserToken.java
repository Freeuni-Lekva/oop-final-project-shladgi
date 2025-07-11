package objects.user;

import databases.annotations.Column;
import databases.annotations.Table;

import java.time.LocalDateTime;

@Table(name = "user_tokens")
public class UserToken {
    @Column(name = "id", primary = true)
    private int id;
    @Column(name = "token")
    private String token;
    @Column(name = "userid")
    private int userid;
    @Column(name = "expiredate")
    private LocalDateTime expiredate;

    public UserToken() {}
    public UserToken(int id,String token, int userid, LocalDateTime expiredate) {
        this.id = id;
        this.token = token;
        this.userid = userid;
        this.expiredate = expiredate;
    }
    public UserToken(String token, int userid, LocalDateTime expiredate) {
        this.token = token;
        this.userid = userid;
        this.expiredate = expiredate;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public int getUserid() {
        return userid;
    }
    public void setUserid(int userid) {
        this.userid = userid;
    }
    public LocalDateTime getExpiredate() {
        return expiredate;
    }
    public void setExpiredate(LocalDateTime expiredate) {
        this.expiredate = expiredate;
    }

}
