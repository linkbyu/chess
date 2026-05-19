package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void addGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void updateGame(int desiredGameID, GameData newGame) throws DataAccessException;

    void deleteGame(GameData game) throws DataAccessException;
}
