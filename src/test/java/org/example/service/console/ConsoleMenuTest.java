package org.example.service.console;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Scanner;
import org.example.factory.MenuFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConsoleMenuTest {

  private static MenuFactory factory = MenuFactory.getInstance(new Scanner(System.in));

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void printWelcomeMessage(Class clazz) {

  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void printMenu(Class clazz) {
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