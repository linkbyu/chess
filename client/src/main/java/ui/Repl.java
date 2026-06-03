package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade facade;
    private ClientUI client;

    public Repl(String serverUrl) {
        facade = new ServerFacade(serverUrl);
        client = new PreloginUI(facade);
    }


    private AuthData userAuth;

    public void run() {
        System.out.println( client.help() );
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while ( doesNotQuit(result) ) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);

            } catch (Exception ex) {
                System.out.print(ex.getMessage());
            }
            System.out.println();
        }
    }

    private boolean doesNotQuit(String result) {
        return !result.equals("q") && !result.equals("quit");
    }

    private void printPrompt() {
        System.out.print(client.replIcon + RESET_TEXT_COLOR + " >>> " + SET_TEXT_COLOR_DARK_GREY);
    }


    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

            String result = client.commandMenu(command, params);
            conditionalSwitchUI(command, params);

            return result;

            } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private boolean ShiftUIFlag;

    private void conditionalSwitchUI(String command, String[] params) throws ResponseException {
        if (ShiftUIFlag) {
            switch (command) {
                case "register", "login":
                    userAuth = client.getAuth();
                    client = new PostloginUI(facade, userAuth);
                    break;
                case "logout":
                    userAuth = null;
                    client = new PreloginUI(facade);
                    break;
                case "join", "observe":
                    var gameList = facade.listGames(userAuth.authToken()).gameList();
                    var desiredGame = gameList.get(Integer.parseInt(params[0]) - 1);
                    client = new GameUI(facade, userAuth, desiredGame);
                    break;
            }
            ShiftUIFlag = false;
        }
    }

    // TO-DO : why is this function not working
    void setShiftUIFlag(boolean shiftUIFlag) {
        this.ShiftUIFlag = shiftUIFlag;
    }
}
