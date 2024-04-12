package org.example.services;

import static org.example.util.Util.getBook;
import static org.example.util.Util.getReader;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RegistryServiceTest {

  private static final RegistryRepository repository = Mockito.mock(RegistryRepository.class);
  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static RegistryService service;

  @BeforeAll
  static void setUpOutput() {
    System.setOut(new PrintStream(output));
  }

  @BeforeEach
  void setUp() {
    output.reset();
  }

  @AfterAll
  static void resetOutput() {
    System.setOut(System.out);
  }

  @Test
  void shouldPrintThereIsNoSuchReaderWhenOptionalReaderEmpty(){
    Optional<Reader> reader = Optional.empty();
    Optional<Book> book = Optional.of(getBook());

    service.borrowBook(book, reader);
    service.returnBook(book, reader);
    service.getAllBorrowedBooksByReader(reader);

    String[] messages = output.toString().split("\n");
    assertTrue(Arrays.stream(messages).allMatch(s -> s.compareToIgnoreCase("There is no such reader!") == 0));

  }

  @Test
  void shouldPrintThereIsNoSuchBookWhenOptionalBookEmpty(){
    Optional<Reader> reader = Optional.of(getReader());
    Optional<Book> book = Optional.empty();

    service.borrowBook(book, reader);
    service.returnBook(book, reader);
    service.getAllBorrowedBooksByReader(reader);

    String[] messages = output.toString().split("\n");
    assertTrue(Arrays.stream(messages).allMatch(s -> s.compareToIgnoreCase("There is no such book!") == 0));
  }

  @Test
  void shouldPrintThatUserCantBorrowBookIfItBorrowed(){
    when(repository.returnBook(any(), any())).thenThrow(new RegistryRepositoryException());
    service.borrowBook(Optional.of(getBook()), Optional.of(getReader()));
    assertEquals("Book is already borrowed! You can't borrow it", output.toString());
  }

  @Test
  void shouldPrintThatReaderBorrowBookSuccessful(){
    when(repository.returnBook(any(), any())).thenThrow(new RegistryRepositoryException());
    service.borrowBook(Optional.of(getBook()), Optional.of(getReader()));
    assertEquals("Book is already borrowed! You can't borrow it", output.toString());
  }

  @Test
  void shouldPrintMessageReaderDidNotBorrowBooksIfListEmpty(){
    fail();
  }

  @Test
  void shouldPrintListOfReaderBorrowedBooks(){
    fail();
  }

  @Test
  void shouldPrintCurrentReaderOfBook(){
    fail();
  }

  @Test
  void shouldPrintThatBookNobodyReads(){
    fail();
  }

}