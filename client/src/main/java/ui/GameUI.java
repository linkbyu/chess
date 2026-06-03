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

import static ui.EscapeSequences.*;

public final class GameUI extends ClientUI {

    private GameData gameData;
    private ChessGame game;

    public GameUI(ServerFacade server, AuthData authData, GameData gameData) {
        super(server, authData);
        this.gameData = gameData;
        game = gameData.game();
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
    void commandMenu(String command, String[] params) throws ResponseException {
        switch(command) {
            case "draw" -> printBoardSetup();
            case "leave" -> {

            }
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                help();
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

    private static final int BOARD_LENGTH_LIMIT_IN_SQUARES = 9;

    private void drawLetterLabels(PrintStream out) {
        setBorderColor(out);
        emptyBorderSquare(out);

        String[] letterLabels = { "a", "b", "c", "d", "e", "f", "g", "h" };
        for (int boardCol = 1; boardCol < BOARD_LENGTH_LIMIT_IN_SQUARES; ++boardCol) {
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

    private boolean evenRow;

    private void drawChessBoard(PrintStream out, TeamColor teamColor) {
        setBorderColor(out);

        int[] rowLabels = switch(teamColor){
            case WHITE -> new int[] {1, 2, 3, 4, 5, 6, 7, 8};
            case BLACK -> new int[] {8, 7, 6, 5, 4, 3, 2, 1};
        };

        for (int boardRow = 0; boardRow < BOARD_LENGTH_LIMIT_IN_SQUARES; ++boardRow) {
            int rowNum = rowLabels[boardRow];
            evenRow = (rowNum % 2) == 0;

            printRowLabelSquare(out, rowNum);

            for (int boardCol = 1; boardCol < BOARD_LENGTH_LIMIT_IN_SQUARES; ++boardCol) {
                setBoardSquareColor(out, evenRow, boardCol);
                printBoardSquare(out, new ChessPosition(boardRow, boardCol));
            }


            printRowLabelSquare(out, rowNum);
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
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private void setLightSquareColor(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private void printRowLabelSquare(PrintStream out, int rowNum) {
        setBorderColor(out);
        out.print(EMPTY);
        out.print(rowNum);
        out.print(EMPTY);

    }

    private void printBoardSquare(PrintStream out, ChessPosition position) {
        out.print(EMPTY);

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

        out.print(EMPTY);
    }





}
