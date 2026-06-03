package server;

import com.google.gson.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import model.GameCatalog;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.List;

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

        List<GameData> games = gameService.listGames();
        var gameList = new GameCatalog(games);
        context.result(new Gson().toJson(gameList));
    }
}
