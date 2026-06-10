package ui;

import chess.*;
import client.websocket.MessageHandler;
import client.ServerFacade;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import chess.ChessGame.TeamColor;
import websocket.messages.NotificationMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public final class GameUI extends ClientUI implements MessageHandler {

    private final GameData gameData;
    private final ChessGame game;
    private final WebSocketFacade ws;

    public GameUI(ServerFacade server, AuthData authData, GameData gameData) throws ResponseException {
        super(server, authData);
        ws = new WebSocketFacade(server.getServerUrl(), this);

        this.gameData = gameData;
        game = gameData.game();
        replIcon = String.format("[Game \"%s\"]", gameData.gameName());
    }

    @Override
    public String help() {
        var builder = new StringBuilder();

        builder.append(helpTextColor("\"redraw\" or \"r\"", "to redraw the board"));
        builder.append(helpTextColor("\"move\" or \"m\" <source> <destination> <promotion piece if applicable>",
                                     "to move a piece"));
        builder.append(helpTextColor("\"highlight\" or \"hl\" <PiecePosition>",
                                    "to highlight the legal moves for a given piece"));
        builder.append(helpTextColor("resign", "to forfeit the game"));
        builder.append(helpTextColor("leave", "to exit game"));
        builder.append(helpTextColor("\"help\" or \"h\"", "show possible commands again"));
        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "redraw", "r" -> printBoardSetup();
            case "leave" -> leave();
            case "move", "m" -> makeMove(params);
            case "highlight", "hl" -> highlightLegalMoves();
            case "resign" -> resign();
            case "help", "h" -> help();
            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                        "Unknown command. Please try again.\n" + help());
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

    private static final String LETTER_SPACER = " ";
    private void drawHorizontalBorder(PrintStream out, String letter) {
        out.print(LETTER_SPACER);
        out.print(letter);
        out.print(LETTER_SPACER);
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
            evenRow = (rowNum % 2) != 0;

            printRowLabelSquare(out, rowNum);

            for (int colNum = 1; colNum < 9; ++colNum) {
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
            evenRow = (rowNum % 2) != 0;

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
        out.print(LETTER_SPACER + rowNum + LETTER_SPACER);

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
                default -> "?";
            };
            out.print(pieceIcon);
        }
        else {
            out.print(EMPTY);
        }
    }


    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.println(SET_TEXT_ITALIC + SET_TEXT_COLOR_DARK_GREY + notificationMessage.getMessage() + RESET_TEXT_ITALIC);
        printPrompt();
    }

    private String leave() throws ResponseException {
        setUiShift(true);
        ws.leaveGame( authData.authToken(), gameData.gameID() );
        return String.format("Leaving \"%s\".", gameData.gameName());
    }

    private String makeMove(String[] params) throws ResponseException {
        ChessMove requestedMove = createChessMove(params);
        ws.makeMove(authData.authToken(), gameData.gameID(), requestedMove);

        ChessPiece selectedPiece = game.getBoard().getPiece(requestedMove.getEndPosition());
        return String.format("Moved %s %s", selectedPiece, requestedMove);
    }

    private ChessMove createChessMove(String[] params) throws ResponseException {
        if (params.length == 2) {
            ChessPosition startPos = createChessPosition(params[0]);
            ChessPosition endPos = createChessPosition(params[1]);
            return new ChessMove(startPos, endPos, null);
        }
        else if (params.length == 3) {
            ChessPosition startPos = createChessPosition(params[0]);
            ChessPosition endPos = createChessPosition(params[1]);
            ChessPiece.PieceType requestedPieceType = translateInputToPieceType(params[2]);
            return new ChessMove(startPos, endPos, requestedPieceType);
        }
        throw new ResponseException(ResponseException.Code.BadRequest,
                "Expected: \"move\" <source> <destination> <promotion piece if applicable>");
    }

    private ChessPosition createChessPosition(String input) throws ResponseException {
        try {
            int col = translateLetterToNum(input.substring(0, 1));
            int row = Integer.parseInt( input.substring(1, 2) );
            if (row < 1 || row > 8 ) {
                throw new NumberFormatException();
            }
            return new ChessPosition(row, col);
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            throw new ResponseException(ResponseException.Code.BadRequest,
                                        "Invalid position on the board. Expected [a-h][1-8]. Ex: \"a5\"");
        }
    }

    private int translateLetterToNum(String letter) throws ResponseException {
        letter = letter.toLowerCase();
        return switch(letter) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                    "Invalid column letter. Expected \"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\", or \"h\"");
        };
    }

    private ChessPiece.PieceType translateInputToPieceType(String input) throws ResponseException {
        input = input.toLowerCase();
        return switch(input) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                    "Invalid promotion piece. Expected \"queen\", \"bishop\", \"rook\", or \"knight\"");
        };
    }

    private String highlightLegalMoves() {

        return "";
    }

    private String resign() throws ResponseException {
        ws.resignGame( authData.authToken(), gameData.gameID() );
        return "Forfeited game.";
    }

}
