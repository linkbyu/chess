package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;
import model.params.LoginRequest;
import model.params.RegisterRequest;

import static ui.EscapeSequences.*;

public class PreloginUI extends ClientUI {

    private ServerFacade server;
    public final String replIcon = SET_TEXT_COLOR_RED + "[LOGGED_OUT]";

    public PreloginUI(ServerFacade server) {
        this.server = server;
    }


    @Override
    public String help() {

        String registerLine = helpTextColor("register <USERNAME> <PASSWORD> <EMAIL>",
                                            "to create an account");
        String loginLine = helpTextColor("login <USERNAME> <PASSWORD>",
                                        "to play chess");
        String quitLine = helpTextColor("quit",
                                        "to exit program");
        String helpLine = helpTextColor("help",
                                        "with possible commands");
        return registerLine + loginLine + quitLine + helpLine + "\n";
    }



    @Override
    public String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "register" -> registerSetup(params);
            case "login" -> loginSetup(params);
            case "quit" -> "quit";
            case "help" -> help();
            default -> {
                System.out.println(SET_TEXT_COLOR_RED + "Unknown command. Please try again." + RESET_TEXT_COLOR);
                yield help();
            }
        };
    }



    private String registerSetup(String... params) throws ResponseException {
        if (params.length == 3) {
            AuthData authData = server.register(new RegisterRequest(params[0], params[1], params[2]));

        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected only: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String loginSetup(String... params) throws ResponseException {
        if (params.length == 2){
            server.login(new LoginRequest(params[0], params[1]));

        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected only: <USERNAME> <PASSWORD>");
    }
}
