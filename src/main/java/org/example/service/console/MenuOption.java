package org.example.service.console;

public class MenuOption {

    private int number;
    private Runnable action;
    private String nameAction;


    public MenuOption(int number, Runnable action, String nameAction) {
        this.number = number;
        this.action = action;
        this.nameAction = nameAction;
    }

    public void doAction() {
        action.run();
    }

    public int getNumber() {
        return number;
    }

    public String getNameAction() {
        return nameAction;
    }

}
