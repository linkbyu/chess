package server;

import com.google.gson.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.Collection;
import java.util.Map;

public class ListGameHandler implements Handler {
    private final UserService userService;
    private final GameService gameService;

    public ListGameHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        String authToken = context.header("authorization");
        userService.verifyAuth(authToken);

        Collection<GameData> games = gameService.listGames();
        var result = Map.of("games", games);
        context.json(new Gson().toJson(result));
    }
}
