package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;

import static ui.EscapeSequences.*;

public class PostloginUI extends ClientUI{

    private ServerFacade server;
    private final String username;
    private final String authToken;
    public static String replICON;

    public PostloginUI(ServerFacade server, AuthData authData) {
        this.server = server;
        username = authData.username();
        authToken = authData.authToken();
        replICON = String.format(SET_TEXT_COLOR_GREEN + "[%s]", username);
    }


    @Override
    public String help() {
        var builder = new StringBuilder();
        builder.append(helpTextColor("create <GameName>", "a new game"));
        builder.append(helpTextColor("list", "show current games"));
        builder.append(helpTextColor("join <ID> [WHITE|BLACK]", "join a game"));

        builder.append(helpTextColor("observe <ID>", "observe a game"));
        builder.append(helpTextColor("logout", "when you are done"));
        builder.append(helpTextColor("help", "show possible commands again"));

        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "list" -> server.listGames(authToken);
            case "join" -> server.joinGame();

            case "create" -> server.createGame();
            case "observe" -> server.observe();

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

}
