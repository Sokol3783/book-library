package org.example.ui;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.example.entity.Book;
import org.example.entity.Reader;

public class BaseConsoleMenu {

  private static boolean terminate = false;
  private static final Scanner scanner = new Scanner(System.in);
  private static final String welcomeMessage = "WELCOME TO THE LIBRARY!";


  public void run() {
    System.out.println(welcomeMessage + getTextMenu());
    while (!terminate) {
      if (scanner.hasNextLine()){
        String line = scanner.nextLine().toLowerCase();
        switch (line) {
          case "1"    -> printBooks();
          case "2"    -> printReaders();
          case "exit" -> exit();
          default     -> printErrInvalidOption();
        }
        if (!terminate) {
          System.out.println(getTextMenu());
        }
      }
    }
  }

  private void exit() {
    terminate = true;
    System.out.println("Goodbye!");
  }

  private void printErrInvalidOption() {
    System.err.println("Invalid option");
  }

  private String getTextMenu() {
    return """

        PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:
        [1] SHOW ALL BOOKS IN THE LIBRARY
        [2] SHOW ALL READERS REGISTERED IN THE LIBRARY
        """;
  }

  private void printReaders() {
    getReaders().stream().sorted(Comparator.comparing(Reader::getId)).
        forEach(s -> System.out.println("ID = " + s.getId() + " name = " + s.getName()));
  }

  private void printBooks() {
    getBooks().stream().sorted(Comparator.comparing(Book::getId)).
        forEach(s -> System.out.println(
            "ID = " + s.getId() + " author = " + s.getAuthor() + " title = " + s.getName()));
  }

  private List<Book> getBooks() {
    return List.of(new Book(1, "Little prince", "Antoine de Saint-Exupéry"),
        new Book(2, "Squealer", "George Orwell"),
        new Book(3, "100 Years of Solitude", "Gabriel García Márquez"));
  }

  private List<Reader> getReaders() {
    return List.of(new Reader(1, "Kent Back"),
        new Reader(2, "Clark Kent"),
        new Reader(3, "Bruce Wayne"));
  }

  public boolean isTerminated() {
    return terminate;
  }

}