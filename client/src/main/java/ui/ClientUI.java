package ui;

import client.ServerFacade;
import exception.ResponseException;
import model.AuthData;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public abstract sealed class ClientUI
        permits GameUI, PostloginUI, PreloginUI {

    protected ServerFacade facade;
    protected AuthData authData;
    public String replIcon = SET_TEXT_COLOR_LIGHT_GREY + "[UNKNOWN]";
    private boolean UIShift;

    public ClientUI(ServerFacade server, AuthData authData) {
        facade = server;
        this.authData = authData;
    }


    public abstract String help();
    abstract String commandMenu(String command, String[] params) throws ResponseException;

    public AuthData getAuth() {
        return authData;
    }

    protected String helpTextColor(String instruction, String explanation) {
        String instructionTextSetup = "  " + SET_TEXT_COLOR_BLUE;
        String explanationTextSetup = " - " + SET_TEXT_COLOR_MAGENTA;

        return instructionTextSetup + instruction + RESET_TEXT_COLOR
                + explanationTextSetup + explanation + RESET_TEXT_COLOR + "\n";
    }


    public boolean isUIShift() {
        return UIShift;
    }

    protected void setUIShift(boolean UIShift) {
        this.UIShift = UIShift;
    }
}
