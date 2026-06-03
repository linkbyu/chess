package client;

import exception.ResponseException;
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

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
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
    void loginSuccess() throws ResponseException {
        var request = new RegisterRequest("player8", "beast", "p8@email.com");
        facade.register(request);

        var authData = facade.login(new LoginRequest("player8", "beast"));
        assertTrue(authData.authToken().length() > 10);
        assertEquals("player8", authData.username());
    }

    @Test
    void loginInvalidPassword() throws ResponseException {
        var request = new RegisterRequest("player8", "beast", "p8@email.com");
        facade.register(request);

        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("player8", "")));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        var request = new RegisterRequest("player8", "beast", "p8@email.com");
        var authData = facade.register(request);

        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () ->
                facade.createGame(authData.authToken(), new CreateRequest("oneMoreGame!")));
    }

    @Test
    void logoutNoAuth() throws ResponseException {
        assertThrows(ResponseException.class, () ->
                facade.logout("bestGuessForAnAuth!Please") );
    }

    @Test
    void createGameSuccess() throws ResponseException {
        var request = new RegisterRequest("player8", "beast", "p8@email.com");
        var authData = facade.register(request);

        var createResult = facade.createGame(authData.authToken(), new CreateRequest("duel123"));
        var gameList = facade.listGames(authData.authToken()).gameList();
        GameData newGame = gameList.getFirst();

        assertEquals(createResult.gameID(), newGame.gameID());
        assertEquals("duel123", newGame.gameName());

    }

}
