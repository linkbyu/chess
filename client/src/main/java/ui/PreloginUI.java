package ui;

import exception.ResponseException;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PreloginUI implements ClientUI {
    public String replIcon = SET_TEXT_COLOR_RED + "[LOGGED_OUT]";



    @Override
    public String help() {
        return """
               
               """;
    }

    @Override
    public boolean doesNotQuit(String result) {
        return !result.equals("q") && !result.equals("quit");
    }

    @Override
    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String command = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command) {
                case "register" -> ;
                case "login" -> ;
                case "quit" -> "quit";
                default -> help();

            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
}
