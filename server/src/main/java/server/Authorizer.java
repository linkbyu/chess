package server;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.UnauthorizedException;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.UserService;

public class Authorizer {
    private final UserService userService;

    public Authorizer(UserService userService) {
        this.userService = userService;
    }

    public void authorize(@NotNull Context context) throws DataAccessException, UnauthorizedException {
        String authToken = context.header("authorization");
        userService.verifyAuth(authToken);
    }
}
