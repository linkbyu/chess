package dataaccess.MySqlDAOs;

import dataaccess.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public abstract class MySqlDAO {

    protected void configureTables(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException sqlEx) {
            throw new DataAccessException(String.format("Unable to configure database: %s", sqlEx.getMessage()), sqlEx);
        }
    }

    protected int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> {}
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


    protected Object executeQuery(String statement, Object... queryParams) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement) ) {
            for (int i = 0; i < queryParams.length; i++) {
                Object param = queryParams[i];
                if (param instanceof String p) ps.setString(i + 1, p);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return readObject(rs);
                }
                return null;
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException("ERROR: Could not execute query", ex);
        }
    }

    protected abstract Object readObject(ResultSet rs) throws SQLException;
}
