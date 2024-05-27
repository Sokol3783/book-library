package org.example.ui;

import java.util.List;
import java.util.Scanner;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;

public class ConsoleMenu {

  private static final String WELCOME_MESSAGE = "WELCOME TO THE LIBRARY!";
  private static final String ENTER_READER_ID_MESSAGE = "Please, enter reader's ID:";
  private static final String ENTER_BOOK_ID_MESSAGE = "Please, enter book's ID:";

  private final Scanner scanner = new Scanner(System.in);
  private final BookService bookService;
  private final ReaderService readerService;
  private final RegistryService registryService;
  private boolean terminated;

  public ConsoleMenu() {
    bookService = new BookService();
    readerService = new ReaderService();
    registryService = new RegistryService(bookService, readerService);
  }

  public ConsoleMenu(BookService bookService, ReaderService readers,
      RegistryService registryService) {
    this.bookService = bookService;
    this.readerService = readers;
    this.registryService = registryService;
  }

  public void run() {
    System.out.println(WELCOME_MESSAGE);
    while (!terminated) {
      try {
        System.out.println(getTextMenu());
        handleOption(scanner.nextLine());
      } catch (RuntimeException e) {
        System.err.println(e.getMessage());
      }
    }
  }


  public boolean isTerminated() {
    return terminated;
  }

  private void handleOption(String option) throws RuntimeException {
    switch (option) {
      case "1" -> printAllBooks();
      case "2" -> printAllReaders();
      case "3" -> addNewReader();
      case "4" -> addNewBook();
      case "5" -> borrowBook();
      case "6" -> returnBook();
      case "7" -> showAllBorrowedByReader();
      case "8" -> showCurrentReaderOfBook();
      case "9" -> showAllReaderWithCurrentlyBorrowedBooks();
      case "10" -> showAllBooksWithBorrowers();
      case "exit" -> exit();
      default -> printErrInvalidOption();
    }
  }

  private void showAllBooksWithBorrowers() {
    var booksWithCurrentReaders = registryService.getAllBooksWithBorrowers();
    System.out.println("\n");
    booksWithCurrentReaders.forEach((book, optionalReader) ->
        optionalReader.ifPresentOrElse(
            reader -> System.out.println(book.toString() + " -> borrower: " + reader),
            () -> System.out.println(book.toString() + " -> available"))
    );
  }

  private void showAllReaderWithCurrentlyBorrowedBooks() {
    var readersWithBorrowedBooks = registryService.getAllReadersWithBorrowedBooks();
    readersWithBorrowedBooks.forEach((reader, borrowedBooks) -> {
      if (borrowedBooks.isEmpty()) {
        System.out.println("\nReader : " + reader + " no books borrowed");
      } else {
        System.out.println("\nReader : " + reader + " list of borrowed books:");
        borrowedBooks.forEach(System.out::println);
      }
    });
  }

  private void printAllReaders() {
    System.out.println("\nList of readers:");
    readerService.findAllReaders().forEach(System.out::println);
  }

  private void printAllBooks() {
    System.out.println("\nList of books:");
    bookService.findAllBooks().forEach(System.out::println);
  }

  private void addNewBook() {
    System.out.println(
        "Please, enter new book name and author separated by “/”. Like this: name / author");
    String newBook = scanner.nextLine();
    Book saved = bookService.addNewBook(newBook);
    System.out.println(saved);
  }

  private void addNewReader() {
    System.out.println("Please enter new reader full name!");
    String newReader = scanner.nextLine();
    Reader reader = readerService.addNewReader(newReader);
    System.out.println(reader.toString());
  }

  private void borrowBook() {
    System.out.println("Please enter book ID and reader ID. Like this: 15 / 15");
    String bookIdAndReaderId = scanner.nextLine();
    Book book = registryService.borrowBook(bookIdAndReaderId);
    System.out.println("Book " + book.getName() + "borrowed.");
  }

  private void returnBook() {
    System.out.println(ENTER_BOOK_ID_MESSAGE);
    String bookId = scanner.nextLine();
    Book book = registryService.returnBook(bookId);
    System.out.println("Book " + book.getName() + " is returned.");
  }

  private void showAllBorrowedByReader() {
    System.out.println(ENTER_READER_ID_MESSAGE);
    String readerId = scanner.nextLine();
    List<Book> borrowedBooks = registryService.findBorrowedBooksByReader(readerId);
    System.out.println("Borrowed books:");
    borrowedBooks.forEach(System.out::println);
  }

  private void showCurrentReaderOfBook() {
    System.out.println(ENTER_BOOK_ID_MESSAGE);
    String readerId = scanner.nextLine();
    Reader reader = registryService.findCurrentReaderOfBook(readerId);
    System.out.println(reader);
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
        [9] SHOW ALL READERS WITH THEIR BORROWED BOOKS
        [10] SHOW ALL BOOKS WITH THEIR CURRENT READERS
        TYPE “EXIT” TO STOP THE PROGRAM AND EXIT!
        """;
  }

}
