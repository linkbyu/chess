package dataaccess.memorydao;

import dataaccess.GameDAO;
import dataaccess.exception.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final List<GameData> gameList;

    public MemoryGameDAO() {
        gameList = new ArrayList<>();
    }

    @Override
    public int addGame(GameData g) throws DataAccessException {
        gameList.add(g);
        return 0;
    }

    @Override
    public GameData getGame(int desiredGameID) throws DataAccessException {
        for (GameData g : gameList){
            if ( desiredGameID == g.gameID() ){
                return g;
            }
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return gameList;
    }

    @Override
    public void updateGame(int desiredGameID, GameData newGame) throws DataAccessException {
        GameData oldGame = getGame(desiredGameID);
        deleteGame(oldGame.gameID());
        addGame(newGame);
    }

    @Override
    public void deleteGame(int gameID) throws DataAccessException {
        var game = getGame(gameID);
        gameList.remove(game);
    }

    @Override
    public void clear() throws DataAccessException {
        gameList.clear();
    }


}
