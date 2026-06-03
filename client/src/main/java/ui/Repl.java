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
        System.out.print( client.help() );
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while ( !result.equals("quit") ) {
            System.out.print(result);
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);

            } catch (Exception ex) {
                System.out.print("  " + SET_TEXT_COLOR_RED + ex.getMessage());
            }
        }
        System.out.println(SET_TEXT_COLOR_BLUE +  "Thanks for playing!");
    }


    private void printPrompt() {
        System.out.print("\n" + client.replIcon + RESET_TEXT_COLOR + " >>> ");
    }


    private String eval(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        String command = (tokens.length > 0) ? tokens[0] : "";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        String result = client.commandMenu(command, params);
        if (client.isUIShift()) {
            switchUI(command, params);
            client.setUIShift(false);
        }
        return result;
    }


        private void switchUI(String command, String[] params) throws ResponseException {
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
            case "leave":
                client = new PostloginUI(facade, userAuth);
                break;
        }
    }

}
