package org.example.ui;

import java.util.Scanner;

public class BaseConsoleMenu {

    private final Scanner scanner;
    private final String welcomeMessage;

    public BaseConsoleMenu() {
        scanner = new Scanner(System.in);
        welcomeMessage = "WELCOME TO THE LIBRARY!";
    }

    public void run() {

    }

}
