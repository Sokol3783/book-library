package org.example.service.console;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class BaseConsoleMenu implements ConsoleMenu {

    private final Map<Integer, MenuOption> map;
    private final String welcomeMessage;
    private final Scanner scanner;

    public BaseConsoleMenu(String welcomeMessage, Scanner scanner, LinkedHashMap<Integer, MenuOption> options) {
        this.welcomeMessage = welcomeMessage;
        this.scanner = scanner;
        this.map = options;
    }

    @Override
    public void printWelcomeMessage() {
        System.out.println(welcomeMessage);
    }

    public void run() {

        printMenu();

        while (true) {
            if (scanner.hasNext()) {
                String input = scanner.nextLine();
                if (input.compareToIgnoreCase("exit") == 0) {
                    break;
                } else if (input.length() == 0) {
                    continue;
                }
                runMenuOption(input);
                printMenu();
            }
        }
    }

    private void runMenuOption(String input) {
        MenuOption option = defaultMenuOption();
        if (input.matches("\\d+")) {
          option = map.getOrDefault(Integer.parseInt(input), option);
        }
        option.doAction();
    }

    public void printMenu() {
        System.out.println("PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:");
        map.entrySet().forEach(s -> System.out.printf("[%2d ] %25s\n", s.getValue().getNumber(), s.getValue().getNameAction()));
        System.out.println("\nTYPE “EXIT” TO STOP THE PROGRAM AND EXIT!");
    }

    private MenuOption defaultMenuOption(){
        return new MenuOption(99, () -> System.out.println("Invalid input\n"), "invalid input");
    }

}
