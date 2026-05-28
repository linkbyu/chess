package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import model.GameData;
import service.params.CreateRequest;
import service.params.JoinRequest;

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

    public int createGame(CreateRequest createRequest) throws DataAccessException {
        String gameName = createRequest.gameName();
        if ( gameName == null || gameName.isBlank() ){
            throw new BadRequestException("Error: no given game name");
        }

        int gameID = abs(new Random().nextInt());
        gameID = gameDAO.addGame(new GameData(gameID, null, null,
                createRequest.gameName(), new ChessGame()));
        return gameID;
    }

    public void joinGame(JoinRequest joinRequest, String username) throws DataAccessException{
        int gameID = joinRequest.gameID();
        GameData game = findGame(gameID);

        var requestedTeam = joinRequest.playerColor();
        if ( requestedTeam == null ){
            throw new BadRequestException("Error: invalid requested team");
        }
        switch( requestedTeam ){
            case WHITE:
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("Error: White team already taken");
                }
                else {
                    gameDAO.updateGame(gameID, new GameData(gameID, username, game.blackUsername(),
                                                            game.gameName(), game.game()));
                }
                break;
            case BLACK:
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("Error: Black team already taken");
                }
                else {
                    gameDAO.updateGame(gameID, new GameData(gameID, game.whiteUsername(), username,
                                                            game.gameName(), game.game()));
                }
                break;
        }
    }

    public GameData findGame(int gameID) throws DataAccessException {
        GameData requestedGame = gameDAO.getGame(gameID);
        if (requestedGame != null){
            return requestedGame;
        }
        else {
            throw new BadRequestException("Error: requested game does not exist");
        }
    }

}
