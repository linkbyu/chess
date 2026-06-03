package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.params.CreateResult;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import model.params.CreateRequest;

public class CreateGameHandler implements Handler {
    private final UserService userService;
    private final GameService gameService;

    public CreateGameHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String authToken = context.header("authorization");
        userService.verifyAuth(authToken);

        var createRequest = new Gson().fromJson(context.body(), CreateRequest.class);
        int gameID = gameService.createGame(createRequest);
        context.result(new Gson().toJson(new CreateResult(gameID)) );
    }
}
