package org.example.service.console;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.example.factory.MenuFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConsoleMenuTest {

  private static MenuFactory factory = MenuFactory.getInstance(new Scanner(System.in));
  private ByteArrayOutputStream output = new ByteArrayOutputStream();

  @BeforeEach
  void setUpStream() {
    System.setOut(new PrintStream(output));
  }

  @AfterEach
  void cleanUpStreams() {
    System.setOut(null);
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

    assertTrue(printedText.contains("exit"));
    assertTrue(printedText.contains(""));

  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void run(Class clazz) {
  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void isSingleton(Class clazz) {
    ConsoleMenu menu = factory.getMenu(clazz);
    ConsoleMenu menu2 = factory.getMenu(clazz);

    assertTrue(ConsoleMenu.class.isInstance(menu));
    assertTrue(ConsoleMenu.class.isInstance(menu2));

    assertNotNull(menu);
    assertNotNull(menu2);

    assertEquals(menu, menu2);
  }

}