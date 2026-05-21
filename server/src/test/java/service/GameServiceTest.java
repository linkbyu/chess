package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.params.CreateRequest;

public class GameServiceTest {
    private GameService gameService;

    @BeforeEach
    void setUp(){
        gameService = new GameService(new MemoryUserDAO(), new MemoryGameDAO());
    }

    @Test
    void listNoGames() throws DataAccessException {
        Assertions.assertTrue(gameService.listGames().isEmpty());
    }

    @Test
    void listAddedGames() throws BadRequestException, DataAccessException {
        int gameID1 = gameService.createGame(new CreateRequest("fight!"));
        int gameID2 = gameService.createGame(new CreateRequest("practice"));
        int gameID3 = gameService.createGame(new CreateRequest("Derek's Room"));

        var games = gameService.listGames();
        Assertions.assertFalse( games.isEmpty() );

    }

    @Test
    void createGameNoGameNameGiven(){
        String gameName = "   ";
        Assertions.assertThrows(BadRequestException.class, () ->
                gameService.createGame(new CreateRequest(gameName)));
    }

    @Test
    void createGameSuccess() throws DataAccessException, BadRequestException {
        String gameName = "practice";
        var gameID = gameService.createGame(new CreateRequest(gameName));


    }

    @Test
    void findNonexistentGame(){
        Assertions.assertThrows(DataAccessException.class, () ->
                gameService.findGame(123));
    }

    @Test
    void findGameSuccess() throws BadRequestException, DataAccessException {
        gameService.createGame(new CreateRequest("win!!"));
        gameService.createGame(new CreateRequest("glhf"));

        String gameName = "practice";
        var gameID = gameService.createGame(new CreateRequest(gameName));

        GameData game = gameService.findGame(gameID);
        Assertions.assertEquals( gameName, game.gameName() );
        Assertions.assertEquals( gameID, game.gameID() );
        Assertions.assertNull(game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertEquals(new ChessGame(), game.game());
    }


}
