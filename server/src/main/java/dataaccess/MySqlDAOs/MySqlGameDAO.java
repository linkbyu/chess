package dataaccess.MySqlDAOs;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.GameDAO;
import dataaccess.exception.DataAccessException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlGameDAO extends MySqlDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        super.configureTables(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              gameID int NOT NULL PRIMARY KEY AUTO_INCREMENT,
              whiteUsername varchar(32) NULL,
              blackUsername varchar(32) NULL,
              gameName varchar(32) NOT NULL,
              chessGame text NOT NULL
            );
            """
    };


    @Override
    public int addGame(GameData game) throws DataAccessException {
        var statement =
                "INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";

        return executeUpdate( statement, game.whiteUsername(), game.blackUsername(),
                                            game.gameName(), game.game() );
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE gameID=?";

        try {
            var gameData = (GameData) executeQuery(statement, Integer.valueOf(gameID) );
            if (gameData != null){
                return gameData;
            }
            else {
                throw new DataAccessException("Game does not exist!");
            }
        } catch (Exception e) {
            throw new DataAccessException("Game does not exist!", e);
        }
    }

    @Override
    protected GameData readObject(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");

        var json = rs.getString("chessGame");
        ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        var statement = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(statement);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(readObject(rs));
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void updateGame(int gameID, GameData newGame) throws DataAccessException {
        var statement = "UPDATE games SET chessGame=? WHERE gameID=?";
        executeUpdate(statement, newGame.game(), Integer.valueOf(gameID) );
    }

    @Override
    public void deleteGame(GameData game) throws DataAccessException {
        var statement = "DELETE FROM games WHERE gameID=?";
        int gameID = game.gameID();
        executeUpdate(statement, Integer.valueOf(gameID));
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

}
