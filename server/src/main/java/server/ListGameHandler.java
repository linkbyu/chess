package server;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;

import java.util.Collection;

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
        var builder = new StringBuilder();
        builder.append("{ \"games\": [");
        if (!games.isEmpty()) {
            for (GameData game : games) {
                /*
                builder.append("[{\"gameID\": ");
                builder.append(game.gameID());

                builder.append(", \"whiteUsername\":");
                builder.append(game.whiteUsername());

                builder.append(", \"blackUsername\":");
                builder.append(game.blackUsername());

                builder.append(", \"gameName:");
                builder.append(game.gameName());
                builder.append("} ]");
                */
                builder.append(game.toString());
            }
        }

        builder.append(" ]}");
        context.json(builder.toString());
    }
}
