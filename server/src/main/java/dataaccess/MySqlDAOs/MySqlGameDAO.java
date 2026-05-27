package dataaccess.MySqlDAOs;

import dataaccess.GameDAO;
import dataaccess.exception.DataAccessException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlGameDAO extends MySqlDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        super.configureTables(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL PRIMARY KEY AUTO_INCREMENT,
              `whiteUsername` varchar(32) NOT NULL,
              `blackUsername` varchar(32) NOT NULL,
              `gameName` varchar(32) NOT NULL,
              `chessGame` varchar(128) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    @Override
    public void addGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    protected Object readObject(ResultSet rs) throws SQLException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(int desiredGameID, GameData newGame) throws DataAccessException {

    }

    @Override
    public void deleteGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

}
