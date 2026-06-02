package ui;

import static ui.EscapeSequences.SET_TEXT_COLOR_DARK_GREY;

public interface ClientUI {

    String help();
    boolean doesNotQuit(String result);
    String replIcon = SET_TEXT_COLOR_DARK_GREY + "[UNKNOWN]";
    String eval(String input);
}
