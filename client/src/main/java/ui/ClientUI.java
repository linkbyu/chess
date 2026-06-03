package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public abstract class ClientUI {

    protected ServerFacade server;
    protected AuthData authData;
    public String replIcon = SET_TEXT_COLOR_DARK_GREY + "[UNKNOWN]";

    public ClientUI(ServerFacade server, AuthData authData) {
        this.server = server;
        this.authData = authData;
    }


    public abstract String help();
    abstract String commandMenu(String command, String[] params) throws ResponseException;

    public AuthData getAuth() {
        return authData;
    }

    protected String helpTextColor(String instruction, String explanation) {
        String instructionTextSetup = "  " + SET_BG_COLOR_BLUE;
        String explanationTextSetup = " - " + SET_BG_COLOR_MAGENTA;

        return instructionTextSetup + instruction + RESET_TEXT_COLOR
                + explanationTextSetup + explanation + RESET_TEXT_COLOR + "\n";
    }
}
