package ui;

import chess.ChessGame;
import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public final class GameUI extends ClientUI {

    private GameData gameData;

    public GameUI(ServerFacade server, AuthData authData, GameData gameData) {
        super(server, authData);
        this.gameData = gameData;
    }

    @Override
    public String help() {
        var builder = new StringBuilder();

        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "quit" -> "quit";
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                yield help();
            }
        };
    }

    private String printBoard(ChessGame.TeamColor teamColor) {
        return "";
    }
}
