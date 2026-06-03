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

    public PostloginUI(ServerFacade facade, AuthData authData) {
        super(facade, authData);
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
    void commandMenu(String command, String[] params) throws ResponseException {
        switch(command) {
            case "list" -> facade.listGames(authToken);
            //case "join" -> server.joinGame();

            case "create" -> createGame(params[0]);
            //case "observe" -> server.observe();

            case "logout" -> {
                facade.logout(authToken);
                setUIShift(true);
            }
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                help();
            }

        };
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            facade.createGame(authToken, new CreateRequest(params[0]));

            return "";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected only: <GAME_NAME>");
    }

}
