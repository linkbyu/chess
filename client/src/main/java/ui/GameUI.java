package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import chess.ChessGame.TeamColor;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public final class GameUI extends ClientUI {

    private GameData gameData;

    public GameUI(ServerFacade server, AuthData authData, GameData gameData) {
        super(server, authData);
        this.gameData = gameData;
    }

    @Override
    public String help() {


        var builder = new StringBuilder();

        builder.append(helpTextColor("draw", "to redraw the board"));
        builder.append(helpTextColor("leave", "to exit game"));
        builder.append(helpTextColor("help", "show possible commands again"));
        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "draw" -> printBoardSetup();
            case "leave" -> "leave";
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                yield help();
            }
        };
    }

    private void printBoardSetup() {
        String username = authData.username();
        String blackUsername = gameData.blackUsername();

        TeamColor team;
        if ( username.equals(blackUsername) ) {
            team = TeamColor.BLACK;
        }
        else {
            team = TeamColor.WHITE;
        }

        printBoard(team);
    }

    private void printBoard(TeamColor teamColor) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawLetterLabels(out);

        drawChessBoard(out, teamColor);

        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);

    }

    private static final int BOARD_SIDE_LENGTH_IN_SQUARES = 8;

    private void drawLetterLabels(PrintStream out) {
        setBorderColor(out);
        emptyBorderSquare(out);

        String[] letterLabels = { "a", "b", "c", "d", "e", "f", "g", "h" };
        for (int boardCol = 0; boardCol < BOARD_SIDE_LENGTH_IN_SQUARES; ++boardCol) {
            drawHorizontalBorder(out, letterLabels[boardCol]);
        }

        emptyBorderSquare(out);
        out.println();
    }

    private static void setBorderColor(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private void emptyBorderSquare(PrintStream out) {
        out.print(EMPTY.repeat(3));
    }

    private void drawHorizontalBorder(PrintStream out, String letter) {
        out.print(EMPTY);
        out.print(letter);
        out.print(EMPTY);
    }

    private void drawChessBoard(PrintStream out, TeamColor teamColor) {
        setBorderColor(out);

        int[] rowLabels = switch(teamColor){
            case WHITE -> new int[] {1, 2, 3, 4, 5, 6, 7, 8};
            case BLACK -> new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        };

        printRowLabelSquare(out);
    }





}
