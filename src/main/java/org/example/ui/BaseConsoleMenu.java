package org.example.ui;

import org.example.entity.BookEntity;
import org.example.entity.ReaderEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BaseConsoleMenu {

    private final Scanner scanner;
    private final String welcomeMessage;

    public BaseConsoleMenu() {
        scanner = new Scanner(System.in);
        welcomeMessage = "WELCOME TO THE LIBRARY!";
    }

    public void run() {
        System.out.println(welcomeMessage + "\n" + getTextMenu());
        while (true) {
            if (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (line.compareToIgnoreCase("EXIT") == 0){
                    System.out.println("Goodbye!");
                    break;
                } else {
                    switch (line) {
                        case "1" : printBooks();
                        case "2" : printReaders();
                        default  : printInvalidOption();
                    }
                }
                System.out.println(getTextMenu());
            }
        }
    }

    private void printInvalidOption() {
        System.out.println("Invalid option!");
    }

    private String getTextMenu() {
        return """
                PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:
                [1] SHOW ALL BOOKS IN THE LIBRARY
                [2] SHOW ALL READERS REGISTERED IN THE LIBRARY
                """;
    }

    private void printReaders() {
        String buffer = String.format(" %2s %27s\n", "id", "name") +
                getReaders().stream().sorted(Comparator.comparing(ReaderEntity::getId)).map(
                                s -> String.format(" %4d %-25s", s.getId(), s.getName()))
                        .collect(Collectors.joining("\n"));
        System.out.println(buffer);
    }

    private void printBooks() {
        String buffer = String.format(" %2s %13s %25s\n", "id", "title", "author") +
                getBooks().stream().map(
                                s -> String.format(" %4d %-25s %-25s", s.getId(), s.getName(), s.getAuthor()))
                        .collect(Collectors.joining("\n"));
        System.out.println(buffer);
    }

    private List<BookEntity> getBooks() {
        return List.of(new BookEntity(1,"Little prince", "Antoine de Saint-Exupéry"),
                new BookEntity(2, "Squealer", "George Orwell"),
                new BookEntity(3, "100 Years of Solitude", "Gabriel García Márquez"));
    }

    private List<ReaderEntity> getReaders() {
        return  List.of(new ReaderEntity(1, "Kent Back"),
                new ReaderEntity(2, "Clark Kent"),
                new ReaderEntity(3, "Bruce Wayne"));
    }

}