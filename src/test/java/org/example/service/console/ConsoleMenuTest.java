package org.example.service.console;

import static java.lang.Thread.sleep;
import static org.example.service.console.TestDataUtil.getTestBooks;
import static org.example.service.console.TestDataUtil.getTestsReaders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.factory.MenuFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConsoleMenuTest {

  private static MenuFactory factory;

  private static final BookRepository books =  mock(BookRepository.class);
  private static final ReaderRepository reader = mock(ReaderRepository.class);
  private ByteArrayOutputStream output = new ByteArrayOutputStream();
  private final InputStream systemStream = System.in;

  @BeforeEach
  void setUpStream() {
    factory = MenuFactory.getInstance(new Scanner(System.in));
    System.setOut(new PrintStream(output));
  }

  @AfterEach
  void cleanUpStreams() {
    System.setOut(null);
    System.setIn(systemStream);
  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void printWelcomeMessage(Class clazz) {
      ConsoleMenu menu = factory.getMenu(clazz);
      menu.printWelcomeMessage();

      assertNotEquals(0, output.size());
      assertEquals("WELCOME TO THE LIBRARY!\r\n", output.toString());

  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void printMenu(Class clazz) {
    ConsoleMenu menu = factory.getMenu(clazz);
    menu.printMenu();

    assertNotEquals(0, output.size());
    String printedText = output.toString();

    assertTrue(printedText.contains("EXIT"));
    assertTrue(printedText.contains("1"));
    assertTrue(printedText.contains("2"));
    assertTrue(printedText.contains("PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:"));

  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void whenMenuRunThereNoWelcomeMessage(Class clazz) throws InterruptedException {
    ConsoleMenu menu = factory.getMenu(clazz);
    menu.printWelcomeMessage();

    String welcomeMessage = output.toString();
    output.reset();

    when(books.findAll()).thenReturn(getTestBooks());
    when(reader.findAll()).thenReturn(getTestsReaders());

    Thread thread = new Thread(menu::run);
    thread.start();

    waitForInput("1", "2", "1231231", "EXIT");

    String menuInfo = output.toString();

    assertNotEquals(0, output.size());
    assertFalse(menuInfo.contains(welcomeMessage));
  }

  private void waitForInput(String... data) throws InterruptedException {
    for (String string : data ) {
      System.setIn(new ByteArrayInputStream(string.getBytes()));
      sleep(100);
    }
  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void isSingleton(Class clazz) {
    ConsoleMenu menu = factory.getMenu(clazz);
    ConsoleMenu menu2 = factory.getMenu(clazz);

    assertNotNull(menu);
    assertNotNull(menu2);

    assertEquals(menu, menu2);
  }

}