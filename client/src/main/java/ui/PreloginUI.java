package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.params.LoginRequest;
import model.params.RegisterRequest;

import static ui.EscapeSequences.*;

public final class PreloginUI extends ClientUI {

    public final String replIcon = SET_TEXT_COLOR_RED + "[LOGGED_OUT]";

    public PreloginUI(ServerFacade server) {
        super(server, null);
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
    void commandMenu(String command, String[] params) throws ResponseException {
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
            authData = facade.register(new RegisterRequest(params[0], params[1], params[2]));
            setUIShift(true);
            return "Successfully registered.";
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected only: <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String loginSetup(String... params) throws ResponseException {
        if (params.length == 2){
            authData = facade.login(new LoginRequest(params[0], params[1]));
            setUIShift(true);
            return String.format("Logged in as user %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected only: <USERNAME> <PASSWORD>");
    }
}
