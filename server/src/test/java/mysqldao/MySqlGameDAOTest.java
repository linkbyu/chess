package mysqldao;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.mysqldao.MySqlGameDAO;
import dataaccess.exception.DataAccessException;
import model.GameData;
import org.junit.jupiter.api.*;

public class MySqlGameDAOTest {
    private static MySqlGameDAO gameDAO;


    @BeforeAll
    static void configDatabase() throws DataAccessException {
        gameDAO = new MySqlGameDAO();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO.addGame(new GameData(1, "joe", null,
                            "BringIt", new ChessGame()));
    }

    @AfterEach
    void cleanUp() throws DataAccessException {
        gameDAO.clear();
    }



    @Test
    void addGameNoName(){
        Assertions.assertThrows(DataAccessException.class, () ->
                gameDAO.addGame(new GameData(80, null, null,
                                    null, new ChessGame())) );
    }

    @Test
    void addGameSuccess() throws DataAccessException {
        int gameID = 60;
        String gameName = "duel!!";
        var chessGame = new ChessGame();
        gameID = gameDAO.addGame(new GameData(gameID, null, null, gameName, chessGame));

        var foundGame = gameDAO.getGame(gameID);
        Assertions.assertEquals(gameID, foundGame.gameID() );
        Assertions.assertEquals(gameName, foundGame.gameName() );
        Assertions.assertEquals(chessGame, foundGame.game() );
    }

    @Test
    void getGameNull() throws DataAccessException {
        Assertions.assertNull(gameDAO.getGame(10) );
    }

    @Test
    void getGameSuccess() throws DataAccessException {
        var foundGame = gameDAO.getGame(1);

        Assertions.assertEquals(1, foundGame.gameID());
        Assertions.assertEquals("joe", foundGame.whiteUsername());
        Assertions.assertNull(foundGame.blackUsername());
        Assertions.assertEquals("BringIt", foundGame.gameName());
        Assertions.assertEquals(new ChessGame(), foundGame.game() );
    }

    @Test
    void listGamesEmpty() throws DataAccessException {
        gameDAO.deleteGame(1);
        var gameList = gameDAO.listGames();
        Assertions.assertTrue(gameList.isEmpty());
    }

    @Test
    void listGamesSuccess() throws DataAccessException {
        var gameList = gameDAO.listGames();
        var foundGame = gameList.getFirst();

        Assertions.assertEquals(1, foundGame.gameID());
        Assertions.assertEquals("joe", foundGame.whiteUsername());
        Assertions.assertNull(foundGame.blackUsername());
        Assertions.assertEquals("BringIt", foundGame.gameName());
        Assertions.assertEquals(new ChessGame(), foundGame.game() );
    }

    @Test
    void updateGameNull() throws DataAccessException {
        var newGame = new GameData(10, null, null, "fun", new ChessGame());
        gameDAO.updateGame(10, newGame);

        var foundGame = gameDAO.getGame(1);
        Assertions.assertEquals(1, foundGame.gameID());
        Assertions.assertEquals("joe", foundGame.whiteUsername());
        Assertions.assertNull(foundGame.blackUsername());
        Assertions.assertEquals("BringIt", foundGame.gameName());
        Assertions.assertEquals(new ChessGame(), foundGame.game() );
    }

    @Test
    void updateGameSuccess() throws InvalidMoveException, DataAccessException {
        var newGame = new ChessGame();
        newGame.makeMove(new ChessMove(new ChessPosition(2, 1), new ChessPosition(3, 1), null));
        gameDAO.updateGame(1, new GameData(1, "joe", "tom",
                "BringIt", newGame));

        var gameList = gameDAO.listGames();
        var foundGame = gameList.getFirst();
        Assertions.assertEquals(1, foundGame.gameID());
        Assertions.assertEquals("joe", foundGame.whiteUsername());
        Assertions.assertEquals("tom", foundGame.blackUsername());
        Assertions.assertEquals("BringIt", foundGame.gameName());
        Assertions.assertEquals(newGame, foundGame.game() );
    }

    @Test
    void deleteGameNull() throws DataAccessException {
        gameDAO.deleteGame(10);

        var foundGame = gameDAO.getGame(1);
        Assertions.assertEquals(1, foundGame.gameID());
        Assertions.assertEquals("joe", foundGame.whiteUsername());
        Assertions.assertNull(foundGame.blackUsername());
        Assertions.assertEquals("BringIt", foundGame.gameName());
        Assertions.assertEquals(new ChessGame(), foundGame.game() );
    }

    @Test
    void deleteGameSuccess() throws DataAccessException {
        gameDAO.deleteGame(1);
        Assertions.assertNull(gameDAO.getGame(1) );;
    }

    @Test
    void clearGamesSuccess() throws DataAccessException {
        gameDAO.clear();
        Assertions.assertNull(gameDAO.getGame(1) );
    }

}
