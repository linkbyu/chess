package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.params.LoginRequest;
import model.params.RegisterRequest;

import static ui.EscapeSequences.*;

public final class PreloginUI extends ClientUI {



    public PreloginUI(ServerFacade server) {
        super(server, null);
        replIcon = SET_TEXT_COLOR_LIGHT_GREY + "[LOGGED_OUT]" + RESET_TEXT_COLOR;
    }


    @Override
    public String help() {
        var builder = new StringBuilder();
        builder.append(helpTextColor("\"register\" or \"r\" <USERNAME> <PASSWORD> <EMAIL>",
                                            "to create an account"));
        builder.append(helpTextColor("\"login\" or \"l\" <USERNAME> <PASSWORD>",
                                        "to play chess"));
        builder.append(helpTextColor("\"quit\" or \"q\"", "to exit program"));
        builder.append(helpTextColor("\"help\" or \"h\"", "with possible commands"));

        return builder.toString();
    }



    @Override
    String commandMenu(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "register", "r" -> registerSetup(params);
            case "login", "l" -> loginSetup(params);
            case "quit", "q" -> "quit";
            case "help", "h" -> help();

            default -> throw new ResponseException(ResponseException.Code.BadRequest,
                        "Unknown command. Please try again.\n" + help());
        };
    }



    private String registerSetup(String[] params) throws ResponseException {
        if (params.length == 3) {
            authData = serverFacade.register(new RegisterRequest(params[0], params[1], params[2]));
            setUiShift(true);
            return "Successfully registered.\n" + String.format("Logged in as user %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: \"register\" <USERNAME> <PASSWORD> <EMAIL>");
    }

    private String loginSetup(String[] params) throws ResponseException {
        if (params.length == 2){
            authData = serverFacade.login(new LoginRequest(params[0], params[1]));
            setUiShift(true);
            return String.format("Logged in as user %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.BadRequest, "Expected: \"login\" <USERNAME> <PASSWORD>");
    }
}
