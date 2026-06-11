package ui;

import chess.*;
import client.websocket.MessageHandler;
import client.ServerFacade;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import chess.ChessGame.TeamColor;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.function.Predicate;

import static ui.EscapeSequences.*;
import static ui.Repl.RESPONSE_SPACING;

public final class GameUI extends ClientUI implements MessageHandler {

    private GameData gameData;
    private ChessGame game;
    private ChessMove previousMove;
    private final WebSocketFacade ws;

    public GameUI(ServerFacade server, AuthData authData, GameData gameData) throws ResponseException {
        super(server, authData);
        this.gameData = gameData;
        game = gameData.game();
        previousMove = null;
        swapReplIcon(game);

        ws = new WebSocketFacade(server.getServerUrl(), this);
        ws.connectToGame(authData.authToken(), gameData.gameID());
    }

    @Override
    public String help() {
        var builder = new StringBuilder();
        if (game.isGameOver()) {
                String whiteUsername = gameData.whiteUsername();
                String blackUsername = gameData.blackUsername();
                String winningUsername = game.getWinnerUsername();

                builder.append(SET_TEXT_COLOR_YELLOW + BLACK_ROOK);
            if (winningUsername == null) {
                builder.append(String.format("It's a tie! " +
                        "(Black Team (%s) is in stalemate against White Team (%s))", blackUsername, whiteUsername));
            } else if (winningUsername.equals(whiteUsername)) {
                builder.append(String.format("White Team (%s) won against Black Team (%s)", whiteUsername, blackUsername));
            } else if (winningUsername.equals(blackUsername)) {
                builder.append(String.format("Black Team (%s) won against White Team (%s)", blackUsername, whiteUsername));
            } else {
                builder.append(SET_TEXT_COLOR_RED + "GAME OVER: game status unclear");
            }
            builder.append(SET_TEXT_COLOR_YELLOW + WHITE_ROOK + RESET_TEXT_COLOR + "\n");

            // Commands:
            builder.append(helpTextColor("\"redraw\" or \"r\"", "to redraw the board"));
            builder.append(helpTextColor("\"highlight\" or \"hl\" <PiecePosition>",
                    "to highlight the legal moves for a given piece"));
            builder.append(helpTextColor("\"leave\" or \"l\"", "to exit game"));
            builder.append(helpTextColor("\"help\" or \"h\"", "show possible commands again"));
        }
        else { // the game is ongoing

            builder.append(SET_TEXT_COLOR_WHITE + BLACK_ROOK + "Command Menu: " + WHITE_ROOK +
                            RESET_TEXT_COLOR + "\n");

            builder.append(helpTextColor("\"redraw\" or \"r\"", "to redraw the board"));
            builder.append(helpTextColor("\"move\" or \"m\" <source> <destination> <promotion piece if applicable>",
                    "to move a piece"));
            builder.append(helpTextColor("\"highlight\" or \"hl\" <PiecePosition>",
                    "to highlight the legal moves for a given piece"));
            builder.append(helpTextColor("\"resign\"", "to forfeit the game"));
            builder.append(helpTextColor("\"leave\" or \"l\"", "to exit game"));
            builder.append(helpTextColor("\"help\" or \"h\"", "show possible commands again"));
        }
        return builder.toString();
    }

    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        if (game.isGameOver()) {
            return switch (command) {
                case "redraw", "r" -> printBoardSetup();
                case "leave", "l" -> leave();
                case "highlight", "hl" -> highlightLegalMoves(params);
                case "help", "h" -> help();
                case "move", "m", "resign" ->
                        throw new ResponseException(ResponseException.Code.BadRequest,
                                "That command is not available anymore. The game is over. " +
                                        "Please try again.\n" + help());
                default -> throw new ResponseException(ResponseException.Code.BadRequest,
                        "Unknown command. Please try again.\n" + help());
            };
        }
        else { // game is ongoing
            return switch (command) {
                case "redraw", "r" -> printBoardSetup();
                case "leave", "l" -> leave();
                case "move", "m" -> makeMove(params);
                case "highlight", "hl" -> highlightLegalMoves(params);
                case "resign" -> resign();
                case "help", "h" -> help();
                default -> throw new ResponseException(ResponseException.Code.BadRequest,
                        "Unknown command. Please try again.\n" + help());
            };
        }
    }

    private String printBoardSetup() throws ResponseException {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
        var teamColor = determineTeamToPrint();
        drawLetterLabels(out, teamColor);

        DrawBoardInfo info;
        switch(teamColor) { // set conditions based on team
            case WHITE ->
                info = new DrawBoardInfo(8, row -> row > 0, -1,
                                              1, col -> col < BOARD_LENGTH_LIMIT_IN_SQUARES, 1);

            case BLACK ->
                info = new DrawBoardInfo(1, row -> row < BOARD_LENGTH_LIMIT_IN_SQUARES, 1,
                                              8, col -> col > 0, -1);

            case null -> throw new ResponseException(ResponseException.Code.BadRequest, "No team given.");
        }
        drawChessBoard(out, info);

        drawLetterLabels(out, teamColor);
        return "";
    }

    private TeamColor determineTeamToPrint() {
        String username = authData.username();
        String blackUsername = gameData.blackUsername();

        if ( username.equals(blackUsername) ) {
            return TeamColor.BLACK;
        }
        else {
            return TeamColor.WHITE;
        }

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
        out.print(SET_BG_COLOR_BLUE);
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


    private void drawChessBoard(PrintStream out, DrawBoardInfo info) {
        setBorderColor(out);

        // get params from DrawBoardInfo object
        int rowNum = info.rowNum();
        Predicate<Integer> rowCondition = info.rowCondition();
        int rowIter = info.rowIter();
        boolean evenRow;

        while (rowCondition.test(rowNum)) {
            evenRow = (rowNum % 2) != 0;
            printRowLabelSquare(out, rowNum);

            int colNum = info.colNum();
            Predicate<Integer> colCondition = info.colCondition();
            int colIter = info.colIter();
            while (colCondition.test(colNum)) {
                var currentPosition = new ChessPosition(rowNum, colNum);
                if ( previousMove != null &&
                        ( currentPosition.equals(previousMove.getStartPosition()) ||
                        currentPosition.equals(previousMove.getEndPosition()) ) ) {

                    setPreviousMoveSquareColor(out);
                }
                else {
                    setBoardSquareColor(out, evenRow, colNum);
                }
                printBoardSquare(out, currentPosition);

                colNum += colIter;
            }

            printRowLabelSquare(out, rowNum);
            resetPrintColor(out);
            out.println();

            rowNum += rowIter;
        }
    }

    private void setPreviousMoveSquareColor(PrintStream out) {
        out.print(SET_BG_COLOR_MAGENTA);
        out.print(SET_TEXT_COLOR_BLUE);
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
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private void setLightSquareColor(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private void printRowLabelSquare(PrintStream out, int rowNum) {
        setBorderColor(out);
        out.print(LETTER_SPACER + rowNum + LETTER_SPACER);

    }

    private void printBoardSquare(PrintStream out, ChessPosition position) {

        ChessBoard board = game.getBoard();
        ChessPiece piece = board.getPiece(position);
        if (piece != null) {
            out.print(piece);
        }
        else {
            out.print(EMPTY);
        }
    }


    @Override
    public void notify(NotificationMessage notificationMessage) {
        System.out.println(SET_TEXT_ITALIC + SET_TEXT_COLOR_YELLOW + notificationMessage.getMessage() +
                           RESET_TEXT_ITALIC + RESET_TEXT_COLOR);
        printPrompt();
    }

    @Override
    public void loadGame(LoadGameMessage loadGameMessage) {
        gameData = loadGameMessage.getGameData();
        game = gameData.game();
        previousMove = loadGameMessage.getPreviousMove();

        try {
            Thread.sleep(50); // is there a better way?

            System.out.print("\n\n");
            printBoardSetup();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException ex) {}

        swapReplIcon(game);
    }

    private void swapReplIcon(ChessGame game) {
        if (!game.isGameOver()) {
            replIcon = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE;
            replIcon += switch (game.getTeamTurn()) {
                case WHITE -> String.format("[\"%s\": White's Turn]", gameData.gameName());
                case BLACK -> String.format("[\"%s\": Black's Turn]", gameData.gameName());
            };
            replIcon += RESET_TEXT_COLOR + RESET_BG_COLOR;
        }
        else { // the game is over
            replIcon = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE +
                    String.format("[\"%s\": ", gameData.gameName()) + SET_TEXT_COLOR_RED +
                    "GAME OVER" + SET_TEXT_COLOR_WHITE + "]" +
                    RESET_TEXT_COLOR + RESET_BG_COLOR;
        }
    }

    @Override
    public void showError(ErrorMessage errorMessage) {
        String msg = errorMessage.getErrorMessage();
        System.out.println("\n" + RESPONSE_SPACING + SET_TEXT_COLOR_RED + msg +
                            RESET_TEXT_COLOR);
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

        ChessPiece selectedPiece = game.getBoard().getPiece(requestedMove.getStartPosition());
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

    private String highlightLegalMoves(String[] params) throws ResponseException {
        if (params.length == 1) {
            ChessPosition position = createChessPosition(params[0]);
            ChessPiece requestedPiece = game.getBoard().getPiece(position);
            Collection<ChessMove> possibleMoves = game.validMoves(position);


            return String.format("Highlighted legal moves for %s", requestedPiece);
        }
        throw new ResponseException(ResponseException.Code.BadRequest,
                "Expected: \"hl\" <PiecePosition>");
    }

    private String resign() throws ResponseException {
        ws.resignGame( authData.authToken(), gameData.gameID() );
        return "Forfeited game.";
    }

}
