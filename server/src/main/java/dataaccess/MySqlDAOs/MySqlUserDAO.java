package dataaccess.MySqlDAOs;

import dataaccess.UserDAO;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UserNullException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO extends MySqlDAO implements UserDAO {

    public MySqlUserDAO() throws DataAccessException {
        super.configureTables(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              username varchar(32) NOT NULL PRIMARY KEY,
              password varchar(64) NOT NULL,
              email varchar(128) NULL
            );
            """
    };


    @Override
    public void addUser(UserData u) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(u.password(), BCrypt.gensalt());

        executeUpdate( statement, u.username(), hashedPassword, u.email() );
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT username, password, email FROM users WHERE username=?";

        try {
            UserData userData = (UserData) executeQuery(statement, username);
            if (userData != null){
                return userData;
            }
            else {
                throw new UserNullException("User not in database!");
            }

        } catch (Exception e) {
            throw new UserNullException("User not in database!", e);
        }
    }

    @Override
    protected UserData readObject(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }


}
