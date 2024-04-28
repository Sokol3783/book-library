package org.example.ui;

import java.util.Scanner;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;

public class ConsoleMenu {

  private static final String WELCOME_MESSAGE = "WELCOME TO THE LIBRARY!";
  private static final String ENTER_READER_ID_MESSAGE = "Please, enter reader's ID:";
  private static final String ENTER_BOOK_ID_MESSAGE = "Please, enter book's ID:";

  private final Scanner scanner = new Scanner(System.in);
  private boolean terminated;

  private final BookService books;
  private final ReaderService readers;
  private final RegistryService registry;

  public ConsoleMenu(BookService books, ReaderService readers, RegistryService registry) {
    this.books = books;
    this.readers = readers;
    this.registry = registry;
  }

  public void run() {
    System.out.println(WELCOME_MESSAGE);
    while (!terminated) {
      try {
        handleOption(messageThanReadInput(getTextMenu()));
      } catch (RuntimeException e) {
        System.err.println(e.getMessage());
      }
    }
  }

  private String messageThanReadInput(String message) {
    System.out.println(message);
    return scanner.nextLine().toLowerCase();
  }

  public boolean isTerminated() {
    return terminated;
  }

  private void handleOption(String option) throws RuntimeException {
    switch (option) {
      case "1" -> books.printAllBooks();
      case "2" -> readers.printAllReaders();
      case "3" -> addNewReader();
      case "4" -> addNewBook();
      case "5" -> borrowBook();
      case "6" -> returnBook();
      case "7" -> showAllBorrowedByUser();
      case "8" -> showCurrentReaderOfBook();
      case "exit" -> exit();
      default -> printErrInvalidOption();
    }
  }

  private void addNewBook() {
    System.out.println("Please, enter new book name and author separated by “/”. Like this: name / author")
    books.addNewBook(scanner.nextLine());
  }

  private void addNewReader() {
    System.out.println("Please enter new reader full name!");
    readers.addNewReader(scanner.nextLine());
  }

  private void borrowBook() {
    System.out.println("Please enter book ID and reader ID. Like this: 15 / 15");
    registry.borrowBook(scanner.nextLine());
  }

  private void returnBook() {
    System.out.println(ENTER_BOOK_ID_MESSAGE);
    registry.returnBook(scanner.nextLine());
  }

  private void showAllBorrowedByUser() {
    System.out.println(ENTER_READER_ID_MESSAGE);
    registry.printBorrowedBooksByReader(scanner.nextLine());
  }

  private void showCurrentReaderOfBook() {
    System.out.println(ENTER_BOOK_ID_MESSAGE);
    registry.printCurrentReaderOfBook(scanner.nextLine());
  }

  private void exit() {
    System.out.println("Goodbye!");
    terminated = true;
    scanner.close();
  }

  private void printErrInvalidOption() {
    System.err.println("Invalid option");
  }

  private String getTextMenu() {
    return """
        
        PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:
        [1] SHOW ALL BOOKS IN THE LIBRARY
        [2] SHOW ALL READERS REGISTERED IN THE LIBRARY
        [3] REGISTER NEW READER
        [4] ADD NEW BOOK
        [5] BORROW A BOOK TO A READER
        [6] RETURN A BOOK TO THE LIBRARY
        [7] SHOW ALL BORROWED BOOK BY USER ID
        [8] SHOW CURRENT READER OF A BOOK WITH ID
        TYPE “EXIT” TO STOP THE PROGRAM AND EXIT!
        """;
  }

}
