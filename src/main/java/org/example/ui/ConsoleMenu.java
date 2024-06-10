package org.example.ui;

import java.util.List;
import java.util.Scanner;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleMenu {

  Logger logger = LoggerFactory.getLogger(ConsoleMenu.class);
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
    logger.info(WELCOME_MESSAGE);
    while (!terminated) {
      try {
        logger.info(getTextMenu());
        handleOption(scanner.nextLine());
      } catch (RuntimeException e) {
        logger.error(e.getMessage());
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
    logger.info("\n");
    booksWithCurrentReaders.forEach((book, optionalReader) ->
        optionalReader.ifPresentOrElse(
            reader -> logger.info("{} -> borrower: {}", book.toString(), reader),
            () -> logger.info("{} -> available", book.toString()))
    );
  }

  private void showAllReaderWithCurrentlyBorrowedBooks() {
    var readersWithBorrowedBooks = registryService.getAllReadersWithBorrowedBooks();
    readersWithBorrowedBooks.forEach((reader, borrowedBooks) -> {
      if (borrowedBooks.isEmpty()) {
        logger.info("\nReader : {} no books borrowed", reader);
      } else {
        logger.info("\nReader : {} list of borrowed books:", reader);
        borrowedBooks.forEach(book -> logger.info(book.toString()));
      }
    });
  }

  private void printAllReaders() {
    logger.info("\nList of readers:");
    readerService.findAllReaders().forEach(System.out::println);
  }

  private void printAllBooks() {
    logger.info("\nList of books:");
    bookService.findAllBooks().forEach(System.out::println);
  }

  private void addNewBook() {
    logger.info(
        "Please, enter new book name and author separated by “/”. Like this: name / author");
    String newBook = scanner.nextLine();
    Book saved = bookService.addNewBook(newBook);
    logger.info(saved.toString());
  }

  private void addNewReader() {
    logger.info("Please enter new reader full name!");
    String newReader = scanner.nextLine();
    Reader reader = readerService.addNewReader(newReader);
    logger.info(reader.toString());
  }

  private void borrowBook() {
    logger.info("Please enter book ID and reader ID. Like this: 15 / 15");
    String bookIdAndReaderId = scanner.nextLine();
    Book book = registryService.borrowBook(bookIdAndReaderId);
    logger.info("Book {} borrowed.", book.getName());
  }

  private void returnBook() {
    logger.info(ENTER_BOOK_ID_MESSAGE);
    String bookId = scanner.nextLine();
    Book book = registryService.returnBook(bookId);
    logger.info("Book {} is returned.", book.getName());
  }

  private void showAllBorrowedByReader() {
    logger.info(ENTER_READER_ID_MESSAGE);
    String readerId = scanner.nextLine();
    List<Book> borrowedBooks = registryService.findBorrowedBooksByReader(readerId);
    logger.info("Borrowed books:");
    borrowedBooks.forEach(book -> logger.info(book.toString()));
  }

  private void showCurrentReaderOfBook() {
    logger.info(ENTER_BOOK_ID_MESSAGE);
    String readerId = scanner.nextLine();
    Reader reader = registryService.findCurrentReaderOfBook(readerId);
    logger.info(reader.toString());
  }

  private void exit() {
    logger.info("Goodbye!");
    terminated = true;
    scanner.close();
  }

  private void printErrInvalidOption() {
    logger.error("Invalid option");
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
