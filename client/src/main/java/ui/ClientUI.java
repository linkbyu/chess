package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.Repl.RESPONSE_SPACING;

public abstract sealed class ClientUI
        permits GameUI, PostloginUI, PreloginUI {

    protected ServerFacade serverFacade;
    protected AuthData authData;
    protected String replIcon = SET_TEXT_COLOR_LIGHT_GREY + "[UNKNOWN]";
    private boolean uiShift;

    public ClientUI(ServerFacade server, AuthData authData) {
        serverFacade = server;
        this.authData = authData;
    }


    public abstract String help();
    abstract String commandMenu(String command, String[] params) throws ResponseException;

    public AuthData getAuth() {
        return authData;
    }

    protected String helpTextColor(String instruction, String explanation) {
        String instructionTextSetup = RESPONSE_SPACING + SET_TEXT_COLOR_BLUE;
        String explanationTextSetup = " - " + SET_TEXT_COLOR_MAGENTA;

        return instructionTextSetup + instruction + RESET_TEXT_COLOR
                + explanationTextSetup + explanation + RESET_TEXT_COLOR + "\n";
    }


    public boolean isUiShift() {
        return uiShift;
    }

    protected void setUiShift(boolean uiShift) {
        this.uiShift = uiShift;
    }

    public void printPrompt() {
        System.out.print("\n" + replIcon + RESET_TEXT_COLOR + " >>> ");
    }
}
