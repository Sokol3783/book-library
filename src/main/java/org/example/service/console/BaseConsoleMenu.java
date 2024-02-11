package org.example.service.console;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class BaseConsoleMenu implements ConsoleMenu {

    private final Map<Integer, MenuOption> map = new LinkedHashMap<>();
    private final String welcomeMessage;
    private final Scanner scanner;

    public BaseConsoleMenu(String welcomeMessage, Scanner scanner, Collection<MenuOption> options) {
        this.welcomeMessage = welcomeMessage;
        this.scanner = scanner;
    }

    @Override
    public void printWelcomeMessage() {
        System.out.println(welcomeMessage);
    }

    public void run() {

        printMenu();

        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            if (input.compareToIgnoreCase("exit") == 0) {
                break;
            }
            runMenuOption(input);
            printMenu();
        }
    }

    private void runMenuOption(String input) {
        int i = Integer.parseInt(input);
        MenuOption option = map.getOrDefault(i, defaultMenuOption());
        option.doAction();
    }

    public void printMenu() {
        System.out.println("PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:");
        map.entrySet().forEach(s -> System.out.printf("[%2d] %25s", s.getValue().getNumber(), s.getValue().getNameAction()));
        System.out.println("\nTYPE “EXIT” TO STOP THE PROGRAM AND EXIT!");
    }

    private MenuOption defaultMenuOption(){
        return new MenuOption(99, () -> System.out.println("Invalid input"), "invalid input");
    }

}
