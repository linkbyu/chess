package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exception.AlreadyTakenException;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.jetbrains.annotations.NotNull;
import service.ClearService;
import service.UserService;
import service.params.RegisterRequest;

public class Server {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    private UserService userService;
    private ClearService clearService;

    private final Javalin javalin;

    public Server() {
        initializeDAOs();
        initializeServices(userDAO, gameDAO, authDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .delete("/db", ctx -> clearService.clear() )
                .post("/user", new RegisterHandler(userService) )
                .post("/session", new LoginHandler(userService) )
                .delete("/session", new LogoutHandler(userService) )
                .post("/game", new CreateGameHandler(gameDAO) )
                .exception(Exception.class, new ExceptionHandler() );


    }

    private void initializeDAOs() {
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
    }

    private void initializeServices(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO){
        userService = new UserService(userDAO, authDAO);
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
