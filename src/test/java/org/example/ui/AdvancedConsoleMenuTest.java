package org.example.ui;

import static java.lang.Thread.sleep;
import static org.example.util.Util.inputWithSleep;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.util.Util;
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

  @BeforeAll
  static void setUp() {
    System.setOut(new PrintStream(output));
  }

  private static void run() {
    menu = new AdvancedConsoleMenu(null, null, null);
    menu.run();
  }

  @BeforeEach
  void terminateThreadExecutor() {
    executor = Executors.newSingleThreadExecutor();
  }

  @AfterEach
  void setInExitToMenu() {
    output.reset();
    err.reset();
    executor.shutdownNow();
  }
  @DisplayName("List all books with all new one after input '1'")
  @Test
  void shouldPrintAllBooksAfterInput_1(){
    fail();
  }

  @DisplayName("List all reader with all new one after input '2'")
  @Test
  void shouldPrintAllReadersAfterInput_2(){
    fail();
  }

  @DisplayName("Read input and save new reader after input '3'")
  @Test
  void shouldCreateNewReaderSuccessfulAfterInput_3(){
    fail();
  }

  @DisplayName("Read input and save book after input '4'")
  @Test
  void shouldCreateNewBookSuccessfulAfterInput_4(){
      fail();
  }

  @DisplayName("Borrow book for a specific reader after input '5'" )
  @Test
  void shouldBorrowBookForASpecificReader(){
    fail();
  }

  @DisplayName("Should return a book to the library after input '6'")
  @Test
  void shouldReturnBookToTheLibraryAfterInput_6(){
    fail();
  }

  @DisplayName("List all borrowed books from specific reader after input '7'")
  @Test
  void shouldListAllBorrowedBooksByUserIDAfterInput_7(){
    fail();
  }

  @DisplayName("Show current reader of a book with id after input '8' ")
  @Test
  void shouldPrintCurrentReaderWithBookIdAfterInput_8(){
    fail();
  }

  @Test
  @DisplayName("Menu should print welcome message only once")
  void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException{
    executor.execute(AdvancedConsoleMenuTest::run);
    String welcomeMessage = "WELCOME TO THE LIBRARY!";
    inputWithSleep("1", "2", "dasdgdfhjsa", "5", "exit");
    String outputString = output.toString();
    assertAll( () -> assertTrue(outputString.contains(welcomeMessage)),
        () -> assertEquals(1,countRepeatedSubstrings(outputString, welcomeMessage)));
  }

  @Test
  @DisplayName("Menu shouldn't crash after incorrect input")
  void shouldNotFallDownAfterIncorrectInput() throws InterruptedException {
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("200", "fdasdfhadjs", "asdasdfasdfasd");
    assertAll(() -> assertNotEquals(0,output.size()),
        () -> assertFalse(menu.isTerminated()),
        () -> assertEquals(4, countRepeatedSubstrings(output.toString(), getTextMenu())));
  }

  @Test
  @DisplayName("Close menu after input 'exit'")
  void shouldStopWorkingAfterInputExit() throws InterruptedException {
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("exit", "1", "2");
    String outputString= output.toString();
    assertAll(
        () -> assertNotEquals(0, outputString.length()),
        () -> assertTrue(outputString.contains(getTextMenu())),
        () -> assertFalse(outputString.contains("Kent Back")),
        () -> assertFalse(outputString.contains("Clark Kent")),
        () -> assertFalse(outputString.contains("George Orwell")),
        () -> assertFalse(outputString.contains("Garcia Márquez"))
    );
  }

  @Test
  @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
  void shouldPrintGoodbyeAfterInputExit() throws InterruptedException {
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
  void shouldPrintMenuAfterAnyInputExceptExit() throws InterruptedException {
    executor.execute(AdvancedConsoleMenuTest::run);
    inputWithSleep("1", "2", "3", "exit");
    assertEquals(4, countRepeatedSubstrings(output.toString(), getTextMenu()));
    assertTrue(menu.isTerminated());
  }

  @Test
  @DisplayName("After option or invalid option doesn't print 'Goodbye'")
  void shouldNotPrintReadersAndExitAfterInput_2() throws InterruptedException {
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

  private static int countRepeatedSubstrings(String str, String target) {
    return (str.length() - str.replace(target, "").length()) / target.length();
  }

}