package client;

import chess.*;
import ui.Repl;

import static ui.EscapeSequences.BLACK_QUEEN;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client: " + BLACK_QUEEN);

        String serverUrl = "http://localhost:8080"; // default option
        if (args.length == 1) { // command line specification
            serverUrl = args[0];
        }

        try {
            new Repl(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
