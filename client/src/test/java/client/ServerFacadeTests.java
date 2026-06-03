package client;

import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.params.CreateRequest;
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
    void registerAlreadyTakenUsername() {

    }

    @Test
    void loginSuccess() throws ResponseException {
        var authData = facade.login(new LoginRequest("player8", "beast"));
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player8", authData.username());
    }

    @Test
    void loginInvalidPassword() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("player8", "")));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        facade.logout(exampleAuthData.authToken());
        assertThrows(ResponseException.class, () ->
                facade.createGame(exampleAuthData.authToken(), new CreateRequest("oneMoreGame!")));
    }

    @Test
    void logoutNoAuth() throws ResponseException {
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
    void createGameNoName() throws ResponseException {
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
        GameData game1 = gameList.get(1);
        GameData game2 = gameList.get(2);
        GameData game3 = gameList.get(3);

        assertEquals(game1CreateResult.gameID(), game1.gameID());
        assertEquals(gameName1, game1.gameName());
    }

}
