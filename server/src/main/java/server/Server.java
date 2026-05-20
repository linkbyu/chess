package server;

import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private UserService userService;

    public Server() {
        userService = new UserService();

        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", new RegisterHandler(userService))
                .post("/session", new LoginHandler(userService) )
                .exception();

        // Register your endpoints and exception handlers here.
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
