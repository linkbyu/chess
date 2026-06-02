package ui;

import client.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private ServerFacade server;
    private ClientUI client;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
        client = new PreloginUI();
    }

    public void run() {
        System.out.println( client.help() );
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while ( client.doesNotQuit(result) ) {

            printPrompt();
            String input = scanner.nextLine();


        }
    }


    private void printPrompt() {
        System.out.print(client.replIcon + RESET_TEXT_COLOR + " >>> ");
    }

}
