package service;

import chess.ChessGame;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.params.CreateRequest;
import service.params.JoinRequest;

import static chess.ChessGame.TeamColor.WHITE;

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
        // Note: Uses findGame() to connect our gameIDs to the actual GameData objects
        Assertions.assertTrue( games.contains(gameService.findGame(gameID1)) );
        Assertions.assertTrue( games.contains(gameService.findGame(gameID2)) );
        Assertions.assertTrue( games.contains(gameService.findGame(gameID3)) );
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
        int gameID = gameService.createGame(new CreateRequest(gameName));

        var games = gameService.listGames();
        Assertions.assertFalse( games.isEmpty() );
        GameData game = gameService.findGame(gameID);
        Assertions.assertEquals( gameID, game.gameID() );
    }

    @Test
    void findNonexistentGame(){
        Assertions.assertThrows(BadRequestException.class, () ->
                gameService.findGame(123));
    }

    @Test
    void findGameSuccess() throws BadRequestException, DataAccessException {
        gameService.createGame(new CreateRequest("win!!"));
        gameService.createGame(new CreateRequest("glhf"));

        String gameName = "practice";
        int gameID = gameService.createGame(new CreateRequest(gameName));

        GameData game = gameService.findGame(gameID);
        Assertions.assertEquals( gameName, game.gameName() );
        Assertions.assertEquals( gameID, game.gameID() );
        Assertions.assertNull(game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
        Assertions.assertEquals(new ChessGame(), game.game());
    }

    @Test
    void joinTeamAlreadyTaken() throws BadRequestException, DataAccessException {
        int gameID = gameService.createGame(new CreateRequest("practice"));

        String originalUsername = "joe";
        gameService.joinGame(new JoinRequest(WHITE, gameID), originalUsername);

        String evilUsername = "evilguy145";
        Assertions.assertThrows(AlreadyTakenException.class, () ->
                                gameService.joinGame(new JoinRequest(WHITE, gameID), evilUsername));

        var gameData = gameService.findGame(gameID);
        Assertions.assertEquals(originalUsername, gameData.whiteUsername());
        Assertions.assertNull(gameData.blackUsername());
        Assertions.assertEquals(gameID, gameData.gameID());
        Assertions.assertEquals(new ChessGame(), gameData.game());
    }

    @Test
    void joinOneGameSuccess() throws BadRequestException, DataAccessException {
        int gameID = gameService.createGame(new CreateRequest("practice"));

        String username = "joe";
        gameService.joinGame(new JoinRequest(WHITE, gameID), username);
        var gameData = gameService.findGame(gameID);
        Assertions.assertEquals(username, gameData.whiteUsername());
        Assertions.assertNull(gameData.blackUsername());
        Assertions.assertEquals(gameID, gameData.gameID());
        Assertions.assertEquals(new ChessGame(), gameData.game());

    }


}
