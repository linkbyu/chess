package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import chess.ChessGame.TeamColor;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static ui.EscapeSequences.*;

public final class GameUI extends ClientUI {

    private GameData gameData;
    private ChessGame game;

    public GameUI(ServerFacade server, AuthData authData, GameData gameData) {
        super(server, authData);
        this.gameData = gameData;
        game = gameData.game();
        replIcon = String.format("[Game \"%s\"]", gameData.gameName());
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
            case "leave" -> {
                setUIShift(true);
                yield String.format("Leaving \"%s\"", gameData.gameName());
            }
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                yield help();
            }
        };
    }

    private String printBoardSetup() {
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
        return "";
    }

    private void printBoard(TeamColor teamColor) {

        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        drawLetterLabels(out, teamColor);
        drawChessBoard(out, teamColor);
        drawLetterLabels(out, teamColor);

    }

    private static final int BOARD_LENGTH_LIMIT_IN_SQUARES = 9;

    private void drawLetterLabels(PrintStream out, TeamColor teamColor) {
        setBorderColor(out);
        emptyBorderSquare(out);

        String[] colLabels = switch(teamColor){
            case WHITE -> new String[] { "a", "b", "c", "d", "e", "f", "g", "h" };
            case BLACK -> new String[] { "h", "g", "f", "e", "d", "c", "b", "a"};
        };
        for (int boardCol = 1; boardCol < BOARD_LENGTH_LIMIT_IN_SQUARES; ++boardCol) {
            drawHorizontalBorder(out, colLabels[boardCol - 1]);
        }

        emptyBorderSquare(out);
        resetPrintColor(out);
        out.println();
    }

    private static void setBorderColor(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_DARK_GREY);
    }

    private void emptyBorderSquare(PrintStream out) {
        out.print(EMPTY);
    }

    private void resetPrintColor(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private static final String letterSpacer = " ";
    private void drawHorizontalBorder(PrintStream out, String letter) {
        out.print(letterSpacer);
        out.print(letter);
        out.print(letterSpacer);
    }

    private boolean evenRow;

    private void drawChessBoard(PrintStream out, TeamColor teamColor) {
        setBorderColor(out);
        switch(teamColor) {
            case WHITE -> drawChessBoardWhite(out);
            case BLACK -> drawChessBoardBlack(out);
        }
    }

    private void drawChessBoardWhite(PrintStream out) {
        for (int rowNum = 8; rowNum > 0; --rowNum) {
            //int rowNum = rowLabels.get(boardRow - 1);
            evenRow = (rowNum % 2) == 0;

            printRowLabelSquare(out, rowNum);

            for (int colNum = 8; colNum > 0; --colNum) {
                setBoardSquareColor(out, evenRow, colNum);
                printBoardSquare(out, new ChessPosition(rowNum, colNum));
            }


            printRowLabelSquare(out, rowNum);
            resetPrintColor(out);
            out.println();
        }
    }

    private void drawChessBoardBlack(PrintStream out) {
        for (int rowNum = 1; rowNum < BOARD_LENGTH_LIMIT_IN_SQUARES; ++rowNum) {
            evenRow = (rowNum % 2) == 0;

            printRowLabelSquare(out, rowNum);

            for (int colNum = 1; colNum < BOARD_LENGTH_LIMIT_IN_SQUARES; ++colNum) {
                setBoardSquareColor(out, evenRow, colNum);
                printBoardSquare(out, new ChessPosition(rowNum, colNum));
            }


            printRowLabelSquare(out, rowNum);
            resetPrintColor(out);
            out.println();
        }
    }

    private void setBoardSquareColor(PrintStream out, boolean evenRow, int boardCol) {
        boolean evenCol = (boardCol % 2) == 0;
        if (evenRow) {
            if (evenCol) {
                setLightSquareColor(out);
            }
            else { // Odd Col
                setDarkSquareColor(out);
            }
        }
        else { // Odd Row
            if (evenCol) {
                setDarkSquareColor(out);
            }
            else { // Odd Col
                setLightSquareColor(out);
            }
        }

    }

    private void setDarkSquareColor(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void setLightSquareColor(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private void printRowLabelSquare(PrintStream out, int rowNum) {
        setBorderColor(out);;
        out.print(letterSpacer + rowNum + letterSpacer);

    }

    private void printBoardSquare(PrintStream out, ChessPosition position) {

        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(position);
        if (piece != null) {
            String pieceString = piece.toString();
            String pieceIcon =  switch (pieceString) {
                case "P" -> WHITE_PAWN;
                case "R" -> WHITE_ROOK;
                case "N" -> WHITE_KNIGHT;
                case "B" -> WHITE_BISHOP;
                case "Q" -> WHITE_QUEEN;
                case "K" -> WHITE_KING;

                case "p" -> BLACK_PAWN;
                case "r" -> BLACK_ROOK;
                case "n" -> BLACK_KNIGHT;
                case "b" -> BLACK_BISHOP;
                case "q" -> BLACK_QUEEN;
                case "k" -> BLACK_KING;
                default -> "U";
            };
            out.print(pieceIcon);
        }
        else {
            out.print(EMPTY);
        }
    }





}
