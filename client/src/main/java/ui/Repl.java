package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade serverFacade;
    private ClientUI client;

    public Repl(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        client = new PreloginUI(serverFacade);
    }


    private AuthData userAuth;
    static final String RESPONSE_SPACING = "  ";

    public void run() {
        System.out.print( client.help() );
        Scanner scanner = new Scanner(System.in);

        String result;
        do {
            client.printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (result.equals("quit")) {
                    break;
                }
                System.out.print(result);

                if (client.isUiShift()) {
                    switchUI(command, params);
                    client.setUiShift(false);
                }
            } catch (Exception ex) {
                System.out.print(RESPONSE_SPACING + SET_TEXT_COLOR_RED + ex.getMessage());

            }
        } while ( true );
        System.out.println(SET_TEXT_COLOR_BLUE +  "Thanks for playing!");
    }



    private String command;
    private String[] params;

    private String eval(String input) throws ResponseException {
        String[] tokens = input.toLowerCase().split(" ");
        command = (tokens.length > 0) ? tokens[0] : "";
        params = Arrays.copyOfRange(tokens, 1, tokens.length);

        return client.commandMenu(command, params);
    }


        private void switchUI(String command, String[] params) throws ResponseException {
        switch (command) {
            case "register", "r", "login":
                userAuth = client.getAuth();
                client = new PostloginUI(serverFacade, userAuth);
                break;
            case "l":
                client = switch(client) {
                    case PreloginUI cli -> {
                        userAuth = client.getAuth();
                        yield new PostloginUI(serverFacade, userAuth);
                    }
                    case PostloginUI cli -> {
                        userAuth = null;
                        yield new PreloginUI(serverFacade);
                    }
                    case GameUI cli ->
                            new PostloginUI(serverFacade, userAuth);
                    default -> {
                        userAuth = null;
                        yield new PreloginUI(serverFacade);
                    }
                };
                break;
            case "logout":
                userAuth = null;
                client = new PreloginUI(serverFacade);
                break;
            case "join", "observe":
                var gameList = serverFacade.listGames(userAuth.authToken()).games();
                var desiredGame = gameList.get(Integer.parseInt(params[0]) - 1);
                client = new GameUI(serverFacade, userAuth, desiredGame);

                break;
            case "leave":
                client = new PostloginUI(serverFacade, userAuth);
                break;
        }
        try {
            Thread.sleep(50); // is there a better way?
        } catch (InterruptedException ex) {}

            System.out.print("\n" + client.help());
    }

}
