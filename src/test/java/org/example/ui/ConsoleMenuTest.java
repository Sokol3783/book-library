package org.example.ui;

import static java.lang.Thread.sleep;
import static org.example.util.Util.countRepeatedSubstrings;
import static org.example.util.Util.getBook;
import static org.example.util.Util.getReader;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.getTestReaders;
import static org.example.util.Util.inputWithSleep;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.ConsoleValidationException;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsoleMenuTest {

  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream errArr = new ByteArrayOutputStream();
  private static final PrintStream err = new PrintStream(errArr);
  private static ExecutorService executor = Executors.newSingleThreadExecutor();
  private static ConsoleMenu menu;

  private ReaderService readerService;
  private RegistryService registryService;
  private BookService bookService;

  @BeforeAll
  static void setUp() {
    System.setOut(new PrintStream(output));
  }


  @BeforeEach
  void terminateThreadExecutor() {
    readerService = mock(ReaderService.class);
    bookService = mock(BookService.class);
    registryService = mock(RegistryService.class);
    executor = Executors.newSingleThreadExecutor();
    System.setErr(err);
  }

  @AfterEach
  void setInExitToMenu() throws InterruptedException {
    output.reset();
    errArr.reset();
    System.setErr(System.err);
    executor.shutdownNow();
  }

  private void run() {
    menu = new ConsoleMenu(this.bookService, this.readerService, this.registryService);
    menu.run();
  }

  @DisplayName("Should call BookService.printAllBooks after input '1'")
  @Test
  void shouldPrintAllBooksAfterInput_1() throws InterruptedException {
    when(bookService.findAllBooks()).thenReturn(List.of());
    setInputAndRunMenu("1");
    String header = output.toString();
    assertAll(() -> verify(bookService, times(1)).findAllBooks(),
        () -> assertTrue(header.contains("List of books:")));
  }

  @DisplayName("Should call ReaderService.printAllReader after input '2'")
  @Test
  void shouldPrintAllReadersAfterInput_2() throws InterruptedException {
    when(readerService.findAllReaders()).thenReturn(List.of());
    setInputAndRunMenu("2");
    String header = output.toString();
    assertAll(() -> verify(readerService, times(1)).findAllReaders(),
        () -> assertTrue(header.contains("List of readers:")));
  }

  @DisplayName("Should print message how create new reader and call ReaderService.addNewReader after input '3'")
  @Test
  void shouldPrintMessageHowToDoInputAndCallCreateReaderAfterInput_3() throws InterruptedException {
    Reader reader = getReader();
    when(readerService.addNewReader(anyString())).thenReturn(reader);
    setInputAndRunMenu("3", "ASDAS");
    String message = output.toString();
    assertAll(() -> verify(readerService, times(1)).addNewReader(any()),
        () -> assertTrue(message.contains("Please enter new reader full name!")),
        () -> assertTrue(message.contains(reader.toString()))
    );
  }

  @DisplayName("Read input and save book after input '4'")
  @Test
  void shouldPrintMessageHowToInputAndCallBookServiceAfter_input4() throws InterruptedException {
    Book book = getBook();
    when(bookService.addNewBook(anyString())).thenReturn(book);
    setInputAndRunMenu("4", "15 / 15");
    String message = output.toString();
    assertAll(() -> verify(bookService, times(1)).addNewBook(any()),
        () -> assertTrue(message.contains(
            "Please, enter new book name and author separated by “/”. Like this: name / author")),
        () -> assertTrue(message.contains(book.toString()))
    );
  }

  @DisplayName("Borrow book for a specific reader after input '5'")
  @Test
  void shouldPrintMessageBorrowBookForASpecificReader_5() throws InterruptedException {
    Book book = getBook();
    when(registryService.borrowBook(anyString())).thenReturn(book);
    setInputAndRunMenu("5", "13 / 14");
    String message = output.toString();
    assertAll(() -> verify(registryService, times(1)).borrowBook(any()),
        () -> assertTrue(
            message.contains("Please enter book ID and reader ID. Like this: 15 / 15")),
        () -> assertTrue(message.contains("Book " + book.getName() + "borrowed."))
    );
  }

  @DisplayName("Should return a book to the library after input '6'")
  @Test
  void shouldCallMethodReturnBookToTheLibraryAfterInput_6() throws InterruptedException {
    Book book = getBook();
    when(registryService.returnBook(anyString())).thenReturn(book);
    setInputAndRunMenu("6", "15");
    String message = output.toString();
    assertAll(() -> verify(registryService, times(1)).returnBook(any()),
        () -> assertTrue(message.contains("Please, enter book's ID:")),
        () -> assertTrue(message.contains("Book " + book.getName() + " is returned.")));
  }

  @DisplayName("List all borrowed books from specific reader after input '7'")
  @Test
  void shouldListAllBorrowedBooksByUserIDAfterInput_7() throws InterruptedException {
    List<Book> testBooks = getTestBooks();
    String firstBook = testBooks.get(0).toString();
    String secondBook = testBooks.get(1).toString();
    String thirdBook = testBooks.get(2).toString();
    when(registryService.findBorrowedBooksByReader(anyString())).thenReturn(getTestBooks());
    setInputAndRunMenu("7", "15");
    String message = output.toString();
    assertAll(() -> verify(registryService, times(1)).findBorrowedBooksByReader(any()),
        () -> assertTrue(message.contains("Please, enter reader's ID:")),
        () -> assertTrue(message.contains(firstBook)),
        () -> assertTrue(message.contains(secondBook)),
        () -> assertTrue(message.contains(thirdBook))
    );
  }

  @DisplayName("Show current reader of a book with id after input '8' ")
  @Test
  void shouldPrintCurrentReaderWithBookIdAfterInput_8() throws InterruptedException {
    Reader reader = getReader();
    when(registryService.findCurrentReaderOfBook(anyString())).thenReturn(reader);
    setInputAndRunMenu("8", "30");
    String message = output.toString();
    assertAll(() -> verify(registryService, times(1)).findCurrentReaderOfBook(any()),
        () -> assertTrue(message.contains(reader.toString())));
  }

  @Test
  @DisplayName("Menu should print welcome message only once")
  void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException, IOException {
    String welcomeMessage = "WELCOME TO THE LIBRARY!";
    setInputAndRunMenu("1", "2", "dasdgdfhjsa", "5", "exit");
    String outputString = output.toString();
    assertAll(() -> assertTrue(outputString.contains(welcomeMessage)),
        () -> assertEquals(1, countRepeatedSubstrings(outputString, welcomeMessage)));
  }

  @Test
  @DisplayName("Menu shouldn't crash after incorrect input")
  void shouldNotFallDownAfterIncorrectInput() throws InterruptedException {
    when(readerService.addNewReader(anyString())).thenThrow(new ConsoleValidationException("Invalid input reader"));
    when(bookService.addNewBook(anyString())).thenThrow(new ConsoleValidationException("Invalid input book"));
    when(registryService.findBorrowedBooksByReader(anyString())).thenThrow(new ConsoleValidationException("Invalid input registry"));
    setInputAndRunMenu("200", "3", "asfhasdjf", "4", "dafhdasdjfhasjdf", "7",
        "dsafhgasjdkfhajskdfjasghdfas");
    String errors = errArr.toString();

    assertAll(() -> assertNotEquals(0, output.size()),
        () -> assertFalse(menu.isTerminated()),
        () -> assertTrue(errors.contains("Invalid input reader")),
        () -> assertTrue(errors.contains("Invalid input book")),
        () -> assertTrue(errors.contains("Invalid input registry")),
        () -> assertNotEquals(0, countRepeatedSubstrings(output.toString(), getTextMenu())));
  }

  @Test
  @DisplayName("Close menu after input 'exit'")
  void shouldStopWorkingAfterInputExit() throws InterruptedException {
    setInputAndRunMenu("exit", "1", "2");
    String outputString = output.toString();
    assertAll(
        () -> assertNotEquals(0, outputString.length()),
        () -> assertTrue(outputString.contains(getTextMenu())),
        () -> assertTrue(menu.isTerminated())
    );
  }

  @Test
  @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
  void shouldPrintGoodbyeAfterInputExit() throws InterruptedException {
    setInputAndRunMenu("exit");
    String outputString = output.toString();
    assertTrue(outputString.contains("Goodbye!"));
  }

  @Test
  @DisplayName("Menu should print list of option on startup")
  void shouldPrintListOfOptionOnStartup() throws InterruptedException {
    executor.execute(this::run);
    sleep(500);
    assertTrue(output.toString().contains(getTextMenu()));
  }

  @Test
  @DisplayName("After any input should print menu except exit, after exit stop run menu")
  void shouldPrintMenuAfterAnyInputExceptExit() throws InterruptedException {
    setInputAndRunMenu("1", "2", "exit");
    assertNotEquals(0, countRepeatedSubstrings(output.toString(), getTextMenu()));
    assertTrue(menu.isTerminated());
  }

  @Test
  @DisplayName("After option or invalid option doesn't print 'Goodbye'")
  void shouldPrintReadersAndNotExitAfterAnyInputExceptExit() throws Exception {
    setInputAndRunMenu("2", "1", "10", "asdasdfhasjkdfhdaskd");
    String outputString = output.toString();
    assertFalse(outputString.contains("Goodbye"));
  }

  @Test
  @DisplayName("After invalid option print err to System.err")
  void shouldPrintInvalidOptionToSystemErr() throws InterruptedException {
    setInputAndRunMenu("200", "300", "599");
    String errorMessage = errArr.toString();
    assertAll(() -> assertFalse(errorMessage.isEmpty()),
        () -> assertEquals(3, countRepeatedSubstrings(errorMessage, "Invalid option")));
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

  private void setInputAndRunMenu(String... data) throws InterruptedException {
    inputWithSleep(data);
    executor.execute(this::run);
    sleep(300);
  }

}