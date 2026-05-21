package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import model.GameData;
import service.params.CreateRequest;

import java.util.Collection;
import java.util.Random;

import static java.lang.Math.abs;

public class GameService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public GameData findGame(int gameID) throws DataAccessException {
        GameData requestedGame = gameDAO.getGame(gameID);
        if (requestedGame != null){
            return requestedGame;
        }
        else {
            throw new DataAccessException("Error: requested game does not exist");
        }
    }

    public int createGame(CreateRequest createRequest)
            throws DataAccessException, BadRequestException {
        String gameName = createRequest.gameName();
        if ( gameName == null || gameName.isBlank() ){
            throw new BadRequestException("Error: no given game name");
        }

        int gameID = abs(new Random().nextInt());
        gameDAO.addGame(new GameData(gameID, null, null,
                createRequest.gameName(), new ChessGame()));
        return gameID;
    }

}
