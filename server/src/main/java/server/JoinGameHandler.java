package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.AuthData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import model.params.JoinRequest;

public class JoinGameHandler implements Handler {
    private final UserService userService;
    private final GameService gameService;

    public JoinGameHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String authToken = context.header("authorization");
        AuthData authData = userService.verifyAuth(authToken);

        var joinRequest = new Gson().fromJson(context.body(), JoinRequest.class);
        gameService.joinGame(joinRequest, authData.username());

        context.result("{}");
    }
}
