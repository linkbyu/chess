package ui;

import chess.ChessGame;
import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.params.CreateRequest;
import model.params.JoinRequest;

import java.util.List;

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
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "list" -> listGames();
            case "join" -> joinGame(params[0], params[1]);

            case "create" -> createGame(params[0]);
            case "observe" -> observeGame(params[0], params[1]);

            case "logout" -> logout();
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                yield help();
            }

        };
    }

    private String listGames() throws ResponseException {
        var gameCatalog = facade.listGames(authToken);
        List<GameData> games = gameCatalog.gameList();

        var builder = new StringBuilder();

        for (GameData game : games) {
            builder.append( game.gameID() );
            builder.append(".   Game name: ");
            builder.append( game.gameName() );
            builder.append("    White: ");
            builder.append( game.whiteUsername() );
            builder.append("     Black: ");
            builder.append( game.blackUsername() );
            builder.append(" \n");
        }

        return builder.toString();
    }

    private String createGame(String... params) throws ResponseException {
        if (params.length == 1) {
            facade.createGame(authToken, new CreateRequest(params[0]));

            return "";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected only: <GAME_NAME>");
    }

    private String joinGame(String inputColor, String listGameNum) throws ResponseException {
        ChessGame.TeamColor playerColor = convertStringToTeamColor(inputColor);
        int gameID = Integer.parseInt(listGameNum);

        // int gameID = getGameID(listGameNum)

        facade.joinGame(authToken, new JoinRequest(playerColor, gameID));
        setUIShift(true);
        return String.format("Joined game %d as %s", gameID, username);
    }

    private ChessGame.TeamColor convertStringToTeamColor(String input) {
        return ChessGame.TeamColor.WHITE;
    }

    private String observeGame(String inputColor, String gameIDString) {
        ChessGame.TeamColor playerColor = convertStringToTeamColor(inputColor);
        int gameID = Integer.parseInt(gameIDString);
        return "";
    }

    private String logout() throws ResponseException {
        facade.logout(authToken);
        setUIShift(true);
        return "Logged out.";
    }

}
