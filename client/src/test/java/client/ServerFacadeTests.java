package client;

import chess.ChessGame;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.params.CreateRequest;
import model.params.JoinRequest;
import model.params.LoginRequest;
import model.params.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;


import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;
    private AuthData exampleAuthData;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void setUp() throws ResponseException {
        var request = new RegisterRequest("player8", "beast", "p8@email.com");
        exampleAuthData = facade.register(request);
    }

    @AfterEach
    void cleanUp() throws ResponseException {
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }



    @Test
    void registerSuccess() throws Exception {
        var request = new RegisterRequest("player1", "coolPassword", "p1@email.com");
        var authData = facade.register(request);

        assertTrue(authData.authToken().length() > 10);
        assertEquals("player1", authData.username());
    }

    @Test
    void registerInvalid() {
        var request = new RegisterRequest("", "", "p1@email.com");
        assertThrows(ResponseException.class, () -> facade.register(request));
    }

    @Test
    void registerAlreadyTakenUsername() throws ResponseException {
        var badRequest = new RegisterRequest("player8", "ThisIsMyIdentityNow!", "player8@evil.com");
        assertThrows(ResponseException.class, () -> facade.register(badRequest));

        // checking if original account password still works
        var authData = facade.login(new LoginRequest("player8", "beast"));
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player8", authData.username());
    }

    @Test
    void loginSuccess() throws ResponseException {
        var authData = facade.login(new LoginRequest("player8", "beast"));
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player8", authData.username());
    }

    @Test
    void loginInvalidPassword() {
        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("player8", "")));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        facade.logout(exampleAuthData.authToken());
        assertThrows(ResponseException.class, () ->
                facade.createGame(exampleAuthData.authToken(), new CreateRequest("oneMoreGame!")));
    }

    @Test
    void logoutNoAuth() {
        assertThrows(ResponseException.class, () ->
                facade.logout("bestGuessForAnAuth!Please") );
    }

    @Test
    void createGameSuccess() throws ResponseException {
        var createResult = facade.createGame(exampleAuthData.authToken(), new CreateRequest("duel123"));
        var gameList = facade.listGames(exampleAuthData.authToken()).gameList();
        GameData newGame = gameList.getFirst();

        assertEquals(createResult.gameID(), newGame.gameID());
        assertEquals("duel123", newGame.gameName());

    }

    @Test
    void createGameNoName() {
        assertThrows(ResponseException.class, () ->
                facade.createGame(exampleAuthData.authToken(), new CreateRequest("")) );

    }

    @Test
    void listGamesSuccess() throws ResponseException {
        var gameName1 = "ChallengeMEEE!!!HAHA";
        var gameName2 = "ProsOnly";
        var gameName3 = "GoEasyOnMe!";

        var game1CreateResult = facade.createGame(exampleAuthData.authToken(), new CreateRequest(gameName1));
        var game2CreateResult = facade.createGame(exampleAuthData.authToken(), new CreateRequest(gameName2));
        var game3CreateResult = facade.createGame(exampleAuthData.authToken(), new CreateRequest(gameName3));

        var gameList = facade.listGames(exampleAuthData.authToken()).gameList();
        GameData game1 = gameList.get(0);
        GameData game2 = gameList.get(1);
        GameData game3 = gameList.get(2);

        assertEquals(game1CreateResult.gameID(), game1.gameID());
        assertEquals(gameName1, game1.gameName());
        assertEquals(game2CreateResult.gameID(), game2.gameID());
        assertEquals(gameName2, game2.gameName());
        assertEquals(game3CreateResult.gameID(), game3.gameID());
        assertEquals(gameName3, game3.gameName());
    }

    @Test
    void listGamesEmptyCatalog() throws ResponseException {
        var gameList = facade.listGames(exampleAuthData.authToken()).gameList();
        assertTrue(gameList.isEmpty());
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        var createResult = facade.createGame(exampleAuthData.authToken(), new CreateRequest("practiceWithMe!"));

        facade.joinGame(exampleAuthData.authToken(),
                        new JoinRequest(ChessGame.TeamColor.WHITE, createResult.gameID()));
        var gameList = facade.listGames(exampleAuthData.authToken()).gameList();
        var game = gameList.getFirst();

        assertEquals("practiceWithMe!", game.gameName());
        assertEquals(1, gameList.size());
        assertEquals(createResult.gameID(), game.gameID());

        assertEquals("player8", game.whiteUsername());
        assertNotEquals("a", game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals(new ChessGame(), game.game());
    }

    @Test
    void joinGameAlreadyTaken() throws ResponseException {
        var requestJoe = new RegisterRequest("joe", "joe'sPassword", "joe@joe.com");
        var authDataJoe = facade.register(requestJoe);
        var requestBob = new RegisterRequest("bobby", "bobbums", "bob@joe.com");
        var authDataBob = facade.register(requestBob);

        var createResult = facade.createGame(authDataJoe.authToken(), new CreateRequest("Joe and Bob's room"));
        int gameID = createResult.gameID();

        var joinWhiteRequest = new JoinRequest(ChessGame.TeamColor.WHITE, gameID );
        var joinBlackRequest = new JoinRequest(ChessGame.TeamColor.BLACK, gameID );
        facade.joinGame(authDataJoe.authToken(), joinWhiteRequest);
        facade.joinGame(authDataBob.authToken(), joinBlackRequest);

        var gameList = facade.listGames(exampleAuthData.authToken()).gameList();
        var game = gameList.getFirst();


        assertEquals( gameID, game.gameID() );
        assertEquals( "Joe and Bob's room", game.gameName() );
        assertEquals("joe", game.whiteUsername());
        assertEquals("bobby", game.blackUsername());

        assertThrows(ResponseException.class, () ->
                facade.joinGame(exampleAuthData.authToken(), joinWhiteRequest));
        assertThrows(ResponseException.class, () ->
                facade.joinGame(exampleAuthData.authToken(), joinBlackRequest));

    }

}
