package dataaccess;

import dataaccess.exception.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO{
    private final Collection<GameData> gameList;

    public MemoryGameDAO() {
        gameList = new ArrayList<>();
    }

    @Override
    public void addGame(GameData g) throws DataAccessException {
        gameList.add(g);
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
    public Collection<GameData> listGames() throws DataAccessException {
        return gameList;
    }

    @Override
    public void updateGame(int desiredGameID, GameData newGame) throws DataAccessException {
        GameData oldGame = getGame(desiredGameID);
        deleteGame(oldGame);
        addGame(newGame);
    }

    @Override
    public void deleteGame(GameData game) throws DataAccessException {
        gameList.remove(game);
    }

    @Override
    public void clear() throws DataAccessException {
        gameList.clear();
    }


}
