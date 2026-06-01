package client;

import ui.Prelogin;

import java.util.Arrays;
import java.util.Scanner;

public class Repl {
    private ServerFacade server;
    private Prelogin clientUI;

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);

        var result = "";
        while ( !result.equals("q") | !result.equals("quit")) {

            printPrompt();
            String input = scanner.nextLine();


        }
    }

    private String help() {

    }

    private void printPrompt() {
        System.out.print();
    }

    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = Arrays.stream(tokens).findFirst();
        }
    }
}
