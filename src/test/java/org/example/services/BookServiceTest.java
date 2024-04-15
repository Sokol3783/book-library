package org.example.services;


import static org.example.util.Util.countRepeatedSubstrings;
import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookServiceTest {

  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream err = new ByteArrayOutputStream();

  private static BookService service;
  private static BookRepository repository;

  @BeforeAll
  static void beforeAll() {
    System.setOut(new PrintStream(output));
    System.setErr(new PrintStream(err));
  }

  @AfterAll
  static void afterAll() {
    System.setOut(System.out);
    System.setErr(System.err);
  }

  @BeforeEach
  void setUp() {
    repository = mock(BookRepository.class);
    service = new BookService(repository);
    err.reset();
    output.reset();
  }

  @Test
  void shouldCallRepositoryOnceWhenGetListOfEmptyBooksAndPrintHead() {
    when(repository.findAll()).thenReturn(List.of());
    service.printAllBooks();
    verify(repository, times(1)).findAll();
    assertTrue(output.toString().contains("Books in library:"));
  }

  @Test
  void shouldCallRepositoryOnceWhenGetListOfBooksPrintEvery(){
    List<Book> testBooks = getTestBooks();
    when(repository.findAll()).thenReturn(testBooks);
    service.printAllBooks();
    String message = output.toString();
    verify(repository, times(1)).findAll();
    assertAll(() -> assertTrue(message.contains("Books in library:")),
        () -> assertTrue(message.contains(testBooks.get(0).toString())),
        () -> assertTrue(message.contains(testBooks.get(1).toString())),
        () -> assertTrue(message.contains(testBooks.get(2).toString()))
    );

  }
  @Test
  void shouldPrintThatTitleContainInvalidSymbols(){
    service.addNewBook("ta3#$\\author");
    assertTrue(err.toString().contentEquals("Title contain invalid symbols"));
  }

  @Test
  void shouldPrintThatAuthorContainsInvalidSymbols(){
    service.addNewBook("valid/a$l#uthor");
    assertTrue(err.toString().contentEquals("Author contain invalid symbols"));
  }

  @Test
  void shouldPrintErrThatSizeAuthorNotValid(){
    service.addNewBook("valid/asd");
    service.addNewBook("valid/$l#uyyyyyyyyyyyyesssssssssssssggthor");
    assertEquals(2, countRepeatedSubstrings(err.toString(), "Invalid length of author"));
    assertEquals(2, countRepeatedSubstrings(err.toString(), "Name should contain more than 5 char and less than 30 ones"));
  }

  @Test
  void shoultPrintErrThatSizeTitleNotValid() {
    service.addNewBook("valid/asd");
    service.addNewBook("valid/$l#uyyyyyyyyyyyyesssssssssssssggthor");
    assertEquals(2, countRepeatedSubstrings(err.toString(), "Invalid length of author"));
    assertEquals(2, countRepeatedSubstrings(err.toString(),
        "Name should contain more than 5 char and less than 30 ones"));
  }

  @Test
  void shouldCreateNewBookWithValidFieldsAndPrintMessage() {
    Book book = new Book(1L, "Author 1", "Title 1");
    when(repository.save(book)).thenReturn(book);
    service.addNewBook(book.getName() + "/" + book.getAuthor());
    String message = output.toString();

    assertAll(() -> assertTrue(message.contains("Book saved")),
              () -> assertTrue(message.contains(book.toString())));
  }

  @Test
  void shouldPrintThatTitleNotValid(){
    service.addNewBook("/asisdas");
    service.addNewBook("dsa/a123sdas");
    service.addNewBook("invailid#/author");
    service.addNewBook("bssssssssssssssssssssssssssssssssssssssssssssssssss555555555555555555555555555555555555555555555555555s/sdfasdfas");
    assertEquals(4, countRepeatedSubstrings(err.toString(), "Title not valid"));
  }

  @Test
  void shouldPrintThatAuthorNotValid(){
    service.addNewBook("validtitle/");
    service.addNewBook("validtitle/asda");
    service.addNewBook("validtitle/adas3##$43");
    service.addNewBook("validtitle/iiiiiiiiiiiiiiiijjkk55555555555555551");
    assertEquals(4, countRepeatedSubstrings(err.toString(), "Author not valid"));
  }

}