package c195.Models;
import c195.SqlDriver;
import java.sql.SQLException;

/**
 * User model for handling user login.
 *
 *
 * @author  Austin Anderson
 * @version 1.0
 * @since   2021-06-10
 */
public class User {

    private String userId;
    private String username;
    private String password;

    public User(String userId) throws SQLException {
        SqlDriver db = new SqlDriver();
        String[] user = db.getUser(userId);
        setUserId(user[0]);
        setUsername(user[1]);
        setPassword(user[2]);
    }

    private void setPassword(String s) {
        this.password = s;
    }

    private void setUsername(String s) {
        this.username = s;
    }

    private void setUserId(String s) {
        this.userId = s;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }
}
