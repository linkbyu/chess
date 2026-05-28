package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.MemoryDAOs.MemoryAuthDAO;
import dataaccess.MemoryDAOs.MemoryGameDAO;
import dataaccess.MemoryDAOs.MemoryUserDAO;
import dataaccess.MySqlDAOs.MySqlAuthDAO;
import dataaccess.MySqlDAOs.MySqlGameDAO;
import dataaccess.MySqlDAOs.MySqlUserDAO;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.params.CreateRequest;
import service.params.JoinRequest;
import service.params.LoginRequest;
import service.params.RegisterRequest;

public class ClearServiceTest {

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;


    @BeforeEach
    void setUp() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDAO();
        GameDAO gameDAO = new MySqlGameDAO();
        AuthDAO authDAO = new MySqlAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, gameDAO);
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    @Test
    void clearAllData() throws DataAccessException, BadRequestException {
        userService.register(new RegisterRequest("bob", "hehe", null));
        userService.register(new RegisterRequest("tom", "abc", null));
        int gameID = gameService.createGame(new CreateRequest("duelOfTheFates"));
        gameService.joinGame(new JoinRequest(ChessGame.TeamColor.WHITE, gameID), "tom");
        gameService.joinGame(new JoinRequest(ChessGame.TeamColor.BLACK, gameID), "bob");


        clearService.clear();
        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("bob", "hehe")));
        Assertions.assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("tom", "abc")));
        Assertions.assertTrue( gameService.listGames().isEmpty() );
    }

}
