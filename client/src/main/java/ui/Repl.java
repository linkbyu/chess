package ui;

import client.ServerFacade;
import exception.ResponseException;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ServerFacade server;
    private ClientUI client;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
        client = new PreloginUI(server);
    }


    public void run() {
        System.out.println( client.help() );
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while ( doesNotQuit(result) ) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                //System.out.print(result);
                /*switch(result){
                    case "logout" -> ;
                }*/

            } catch (Exception ex) {
                System.out.print(ex.getMessage());
                //System.out.print(errorMessage(ex));
            }
            System.out.println();
        }
    }

    private boolean doesNotQuit(String result) {
        return !result.equals("q") && !result.equals("quit");
    }


    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return client.commandMenu(command, params);

            } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String errorMessage(Exception ex) {
        return "";
    }


    private void printPrompt() {
        System.out.print(client.replIcon + RESET_TEXT_COLOR + " >>> " + SET_TEXT_COLOR_DARK_GREY);
    }

}
