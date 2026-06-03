package service;

import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import model.GameData;
import model.params.CreateRequest;
import model.params.JoinRequest;

import java.util.Collection;
import java.util.List;

import static java.lang.Math.abs;

public class GameService {
    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames() throws DataAccessException {
        return gameDAO.listGames();
    }

    public int createGame(CreateRequest createRequest) throws DataAccessException {
        String gameName = createRequest.gameName();
        if ( gameName == null || gameName.isBlank() ){
            throw new BadRequestException("no given game name");
        }

        int gameID = gameDAO.addGame(new GameData(1, null, null,
                createRequest.gameName(), new ChessGame()));
        return gameID;
    }

    public void joinGame(JoinRequest joinRequest, String username) throws DataAccessException{
        int gameID = joinRequest.gameID();
        GameData game = findGame(gameID);

        var requestedTeam = joinRequest.playerColor();
        if ( requestedTeam == null ){
            throw new BadRequestException("invalid requested team");
        }
        switch( requestedTeam ){
            case WHITE:
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("White team already taken");
                }
                else {
                    gameDAO.updateGame(gameID, new GameData(gameID, username, game.blackUsername(),
                                                            game.gameName(), game.game()));
                }
                break;
            case BLACK:
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("Black team already taken");
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
            throw new BadRequestException("requested game does not exist");
        }
    }

}
