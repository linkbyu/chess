package dataaccess.MySqlDAOs;

import dataaccess.AuthDAO;
import dataaccess.exception.DataAccessException;
import model.AuthData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlAuthDAO extends MySqlDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        super.configureTables(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
              `username` varchar(32) NOT NULL PRIMARY KEY,
              `authToken` varchar(64) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    protected Object readObject(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
