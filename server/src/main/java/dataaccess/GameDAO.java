package dataaccess;

import dataaccess.exception.DataAccessException;
import model.GameData;

import java.util.Collection;
import java.util.List;

public interface GameDAO {

    int addGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    List<GameData> listGames() throws DataAccessException;

    void updateGame(int desiredGameID, GameData newGame) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    void clear() throws DataAccessException;
}
