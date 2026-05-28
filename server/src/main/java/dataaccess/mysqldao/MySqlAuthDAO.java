package dataaccess.mysqldao;

import dataaccess.AuthDAO;
import dataaccess.exception.DataAccessException;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlAuthDAO extends MySqlDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(32) NOT NULL,
              `authToken` varchar(64) NOT NULL PRIMARY KEY,
            );
            """
        };
        super.configureTables(createStatements);
    }

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();

        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, username, authToken);

        return new AuthData(username, authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth WHERE authToken=?";

        try {
            return (AuthData) executeQuery(statement, authToken );
        } catch (Exception e) {
            throw new DataAccessException("AuthToken does not exist!", e);
        }

    }

    @Override
    protected Object readObject(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var authToken = rs.getString("authToken");
        return new AuthData(username, authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken=?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }
}
