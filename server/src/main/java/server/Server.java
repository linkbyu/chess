package server;

import dataaccess.*;
import dataaccess.MemoryDAOs.MemoryAuthDAO;
import dataaccess.MemoryDAOs.MemoryGameDAO;
import dataaccess.MemoryDAOs.MemoryUserDAO;
import dataaccess.MySqlDAOs.MySqlAuthDAO;
import dataaccess.MySqlDAOs.MySqlGameDAO;
import dataaccess.MySqlDAOs.MySqlUserDAO;
import dataaccess.exception.DataAccessException;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    private final Javalin javalin;

    public Server() {
        try {
            initializeDAOs();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        initializeServices(userDAO, gameDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", ctx -> clearService.clear() )
                .post("/user", new RegisterHandler(userService) )
                .post("/session", new LoginHandler(userService) )
                .delete("/session", new LogoutHandler(userService) )
                .get("/game", new ListGameHandler(userService, gameService) )
                .post("/game", new CreateGameHandler(userService, gameService) )
                .put("/game", new JoinGameHandler(userService, gameService) )
                .exception(Exception.class, new ExceptionHandler() );


    }

    private void initializeDAOs() throws DataAccessException {
        userDAO = new MySqlUserDAO();
        gameDAO = new MySqlGameDAO();
        authDAO = new MySqlAuthDAO();

    }

    private void initializeServices(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, gameDAO);
        clearService = new ClearService(userDAO, gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
