package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.params.CreateRequest;

import static ui.EscapeSequences.*;

public final class PostloginUI extends ClientUI{

    private final String username;
    private final String authToken;
    public static String replICON;

    public PostloginUI(ServerFacade server, AuthData authData) {
        super(server, authData);
        username = authData.username();
        authToken = authData.authToken();
        replICON = String.format(SET_TEXT_COLOR_GREEN + "[%s]", username);
    }


    @Override
    public String help() {
        var builder = new StringBuilder();
        builder.append(helpTextColor("create <GAME_NAME>", "a new game"));
        builder.append(helpTextColor("list", "show current games"));
        builder.append(helpTextColor("join <GAME #> [WHITE|BLACK]", "join a game"));

        builder.append(helpTextColor("observe <GAME #>", "observe a game"));
        builder.append(helpTextColor("logout", "when you are done"));
        builder.append(helpTextColor("help", "show possible commands again"));

        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "list" -> server.listGames(authToken).toString();
            //case "join" -> server.joinGame();

            case "create" -> createGame(params[0]);
            //case "observe" -> server.observe();

            case "logout" -> {
                server.logout(authToken);
                yield "logout";
            }
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                yield help();
            }

        };
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            server.createGame(authToken, new CreateRequest(params[0]));

            return "";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected only: <GAME_NAME>");
    }

}
