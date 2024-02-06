package org.example.service.console;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    private static final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, MenuOption> map = new LinkedHashMap<>();
    private final String welcomeMessage;

    public Menu(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public void run() {

    }

}
