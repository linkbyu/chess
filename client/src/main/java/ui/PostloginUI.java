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

    public PostloginUI(ServerFacade facade, AuthData authData) {
        super(facade, authData);
        username = authData.username();
        authToken = authData.authToken();
        replIcon = String.format(SET_TEXT_COLOR_GREEN + "[%s]", username);
    }


    @Override
    public String help() {
        var builder = new StringBuilder();
        builder.append(helpTextColor("create <GAME_NAME>", "a new game"));
        builder.append(helpTextColor("list", "show current games"));
        builder.append(helpTextColor("join <GAME #> [WHITE|BLACK]", "join a game"));

        builder.append(helpTextColor("observe <GAME #>", "observe a game"));
        builder.append(helpTextColor("logout", "when you are done"));
        builder.append(helpTextColor("\"help\" or \"h\"", "show possible commands again"));

        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "list" -> listGames();
            case "join" -> joinGame(params);

            case "create" -> createGame(params);
            case "observe" -> observeGame(params);

            case "logout" -> logout();
            case "help", "h" -> help();
            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                        "Unknown command. Please try again.\n" + help());

        };
    }

    private String listGames() throws ResponseException {
        var gameCatalog = serverFacade.listGames(authToken);
        List<GameData> games = gameCatalog.games();

        if (!games.isEmpty()) {
            var builder = new StringBuilder();

            for (GameData game : games) {
                builder.append(game.gameID());
                builder.append(".   Game name: ");
                builder.append(game.gameName());
                builder.append("    White: ");
                builder.append(game.whiteUsername());
                builder.append("     Black: ");
                builder.append(game.blackUsername());
                builder.append(" \n");
            }

            return builder.toString();
        }
        else {
            return SET_TEXT_COLOR_YELLOW + "No active games available!";
        }
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            serverFacade.createGame(authToken, new CreateRequest(gameName));

            return String.format("Successfully created game \"%s\".", gameName);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: \"create\" <GAME_NAME>");
    }

    private String joinGame(String[] params) throws ResponseException {
        if (params.length == 2) {
            var desiredGame = getGame(params[0]);
            ChessGame.TeamColor playerColor = convertStringToTeamColor(params[1]);
            int gameID = desiredGame.gameID();
            String gameName = desiredGame.gameName();

            serverFacade.joinGame(authToken, new JoinRequest(playerColor, gameID));
            setUiShift(true);
            String joinedTeam = switch(playerColor){
                case WHITE -> "White";
                case BLACK -> "Black";
            };
            return String.format("Joined game \"%s\" on the %s Team.\n", gameName, joinedTeam);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: \"join\" <GAME #> [WHITE|BLACK]");
    }

    private ChessGame.TeamColor convertStringToTeamColor(String input) throws ResponseException {
        input = input.toLowerCase();
        return switch(input) {
            case "white", "w" -> ChessGame.TeamColor.WHITE;
            case "black", "b" -> ChessGame.TeamColor.BLACK;
            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                        "Invalid [Team]; Expected: \"White\" or \"Black\"");
        };
    }

    private GameData getGame(String listGameNum) throws ResponseException{
        try {
            int requestedNum = Integer.parseInt(listGameNum);
            var gameCatalog = serverFacade.listGames(authToken);
            List<GameData> games = gameCatalog.games();

            return games.get(requestedNum - 1);
        } catch(Exception e){
            throw new ResponseException(ResponseException.Code.BadRequest,
                    "Invalid <GAME #>...Please check the available games again.");
        }
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            var desiredGame = getGame(params[0]);
            String gameName = desiredGame.gameName();

            setUiShift(true);
            return String.format("""
                    Observing game "%s". (From the White Team's view)
                    White: %s
                    Black: %s""", gameName, desiredGame.whiteUsername(), desiredGame.blackUsername());
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: \"observe\" <GAME #>");
    }

    private String logout() throws ResponseException {
        serverFacade.logout(authToken);
        setUiShift(true);
        return "Logged out.";
    }

}
