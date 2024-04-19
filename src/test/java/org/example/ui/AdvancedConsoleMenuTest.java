package org.example.ui;

import static java.lang.Thread.sleep;
import static org.example.util.Util.countRepeatedSubstrings;
import static org.example.util.Util.inputWithSleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdvancedConsoleMenuTest {

  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private static ExecutorService executor = Executors.newSingleThreadExecutor();
  private static AdvancedConsoleMenu menu;

  private static ReaderService reader;
  private static RegistryService registry;
  private static BookService books;

  @BeforeAll
  static void setUp() {
    System.setOut(new PrintStream(output));
  }

  private static void run() {
    menu = new AdvancedConsoleMenu(books, reader, registry);
    menu.run();
  }

  @BeforeEach
  void terminateThreadExecutor() {
    executor = Executors.newSingleThreadExecutor();
    reader = mock(ReaderService.class);
    books = mock(BookService.class);
    registry = mock(RegistryService.class);
  }

  @AfterEach
  void setInExitToMenu() {
    output.reset();
    err.reset();
    executor.shutdownNow();
  }
  @DisplayName("Should call BookService.printAllBooks after input '1'")
  @Test
  void shouldPrintAllBooksAfterInput_1(){
    doNothing().when(books).printAllBooks();
    inputWithSleep("1");
    verify(books, times(1)).printAllBooks();
  }

  @DisplayName("Should call ReaderService.printAllReader after input '2'")
  @Test
  void shouldPrintAllReadersAfterInput_2(){
    doNothing().when(reader).printAllReaders();
    inputWithSleep("2");
    verify(reader, times(1)).printAllReaders();
  }

  @DisplayName("Read input and save book after input '4'")
  @Test
  void shouldPrintMessageHowToInputAndCallsBookServiceAfter_input4(){
    doNothing().when(books).addNewBook(any());
    inputWithSleep("4", "asdasda");
    verify(books, times(1)).addNewBook(any());
  }

  @DisplayName("Borrow book for a specific reader after input '5'" )
  @Test
  void shouldPrintMessageBorrowBookForASpecificReader_5(){
    doNothing().when(registry).borrowBook(any(),any());
    inputWithSleep("5", "adfasdf / adasd");
    verify(registry, times(1)).borrowBook(any(), any());
  }

  @DisplayName("Should return a book to the library after input '6'")
  @Test
  void shouldCallMethodReturnBookToTheLibraryAfterInput_6(){
    doNothing().when(registry).returnBook(any());
    inputWithSleep("6", "adfasdf / adasd");
    verify(registry, times(1)).returnBook(any());
  }

  @DisplayName("List all borrowed books from specific reader after input '7'")
  @Test
  void shouldListAllBorrowedBooksByUserIDAfterInput_7(){
    doNothing().when(registry).printBorrowedBooksByReader(any());
    inputWithSleep("7", "adfasdf / adasd");
    verify(registry, times(1)).printBorrowedBooksByReader(any());
  }

  @DisplayName("Show current reader of a book with id after input '8' ")
  @Test
  void shouldPrintCurrentReaderWithBookIdAfterInput_8(){
    doNothing().when(registry).printCurrentReaderOfBook(any());
    inputWithSleep("8", "adfasdf / adasd");
    verify(registry, times(1)).printCurrentReaderOfBook(any());
  }

  @Test
  @DisplayName("Menu should print welcome message only once")
  void shouldPrintWelcomeMessageOnlyOnce() {
    executor.execute(AdvancedConsoleMenuTest::run);
    String welcomeMessage = "WELCOME TO THE LIBRARY!";
    inputWithSleep("1", "2", "dasdgdfhjsa", "5", "exit");
    String outputString = output.toString();
    assertAll( () -> assertTrue(outputString.contains(welcomeMessage)),
        () -> assertEquals(1,countRepeatedSubstrings(outputString, welcomeMessage)));
  }

  @Test
  @DisplayName("Menu shouldn't crash after incorrect input")
  void shouldNotFallDownAfterIncorrectInput() {
    doNothing().when(reader).addNewReader(any());
    doNothing().when(books).addNewBook(any());
    doNothing().when(registry).printBorrowedBooksByReader(any());
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("200", "fdasdfhadjs", "asdasdfasdfasd", "3", "asfhasdjf", "4", "dafhdasdjfhasjdf", "7", "dsafhgasjdkfhajskdfjasghdfas");
    assertAll(() -> assertNotEquals(0,output.size()),
        () -> assertFalse(menu.isTerminated()),
        () -> assertEquals(4, countRepeatedSubstrings(output.toString(), getTextMenu())));
  }

  @Test
  @DisplayName("Close menu after input 'exit'")
  void shouldStopWorkingAfterInputExit() {
    executor.execute(AdvancedConsoleMenuTest::run);
    boolean start = menu.isTerminated();
    inputWithSleep("exit", "1", "2");
    String outputString= output.toString();
    assertAll(
        () -> assertNotEquals(0, outputString.length()),
        () -> assertTrue(outputString.contains(getTextMenu())),
        () -> assertTrue(start),
        () -> assertTrue(menu.isTerminated())
    );
  }

  @Test
  @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
  void shouldPrintGoodbyeAfterInputExit() {
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("exit");
    String outputString = output.toString();
    assertTrue(outputString.contains("Goodbye!"));
  }

  @Test
  @DisplayName("Menu should print list of option on startup")
  void shouldPrintListOfOptionOnStartup() throws InterruptedException {
    executor.execute(AdvancedConsoleMenuTest::run);
    sleep(100);
    assertTrue(output.toString().contains(getTextMenu()));
  }

  @Test
  @DisplayName("After any input should print menu except exit, after exit stop run menu")
  void shouldPrintMenuAfterAnyInputExceptExit() {
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("1", "2", "exit");
    assertEquals(3, countRepeatedSubstrings(output.toString(), getTextMenu()));
    assertTrue(menu.isTerminated());
  }

  @Test
  @DisplayName("After option or invalid option doesn't print 'Goodbye'")
  void shouldPrintReadersAndNotExitAfterAnyInputExceptExit() {
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("2", "3", "10", "asdasdfhasjkdfhdaskd");
    String outputString = output.toString();
    assertAll(() -> assertFalse(outputString.contains("Goodbye")),
              () -> assertTrue(outputString.contains("name")));
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