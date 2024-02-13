package org.example.service.console;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.entity.BookEntity;
import org.example.entity.ReaderEntity;
import org.example.factory.MenuFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConsoleMenuTest {

  private static MenuFactory factory = MenuFactory.getInstance(new Scanner(System.in));

  private static final BookRepository books =  mock(BookRepository.getInstance());
  private static final ReaderRepository reader = mock(ReaderRepository.getInstance());
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

    assertTrue(printedText.contains("EXIT"));
    assertTrue(printedText.contains("1"));
    assertTrue(printedText.contains("2"));
    assertTrue(printedText.contains("PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:"));

  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void whenMenuRunThereNoWelcomeMessage(Class clazz) {
    ConsoleMenu menu = factory.getMenu(clazz);
    menu.printWelcomeMessage();

    String welcomeMessage = output.toString();
    output = new ByteArrayOutputStream();

    when(books.findAll()).thenReturn(getTestBooks());
    when(reader.findAll()).thenReturn(getTestsReaders());
    menu.run();

    System.out.println(1);
    System.out.println(2);
    System.out.println(1213);
    String menuInfo = output.toString();

    assertFalse(menuInfo.contains(welcomeMessage));
  }

  private Collection<BookEntity> getTestBooks() {
    return List.of( new BookEntity(1, "Test book1", "Test author1"),
    new BookEntity(2, "Test book2", "Test author1"),
        new BookEntity(3, "Test book3", "Test author1"));
  }

  private Collection<ReaderEntity> getTestsReaders() {
    return List.of( new ReaderEntity(1, "Test1"),
        new ReaderEntity(2, "Test2"),
        new ReaderEntity(3, "Test3"));
  }

  @ParameterizedTest
  @ValueSource(classes = {BaseConsoleMenu.class})
  void isSingleton(Class clazz) {
    ConsoleMenu menu = factory.getMenu(clazz);
    ConsoleMenu menu2 = factory.getMenu(clazz);

    assertNotNull(menu);
    assertNotNull(menu2);

    assertTrue(ConsoleMenu.class.isInstance(menu));
    assertTrue(ConsoleMenu.class.isInstance(menu2));


    assertEquals(menu, menu2);
  }

}