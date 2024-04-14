package org.example.services;

import static org.example.util.Util.getBook;
import static org.example.util.Util.getReader;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
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

  private static RegistryRepository repository;
  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static RegistryService service;
  private static final ByteArrayOutputStream err = new ByteArrayOutputStream();

  @BeforeAll
  static void setUpOutput() {
    System.setOut(new PrintStream(output));
    System.setErr(new PrintStream(err));
  }

  @BeforeEach
  void setUp() {
    output.reset();
    err.reset();
    repository = Mockito.mock(RegistryRepository.class);
    service = new RegistryService(repository);
  }

  @AfterAll
  static void resetOutput() {
    System.setOut(System.out);
    System.setOut(System.err);
  }

  @Test
  void shouldPrintThereIsNoSuchReaderWhenOptionalReaderEmpty(){
    Optional<Reader> reader = Optional.empty();
    Optional<Book> book = Optional.of(getBook());

    service.borrowBook(book, reader);
    service.returnBook(book, reader);
    service.getAllBorrowedBooksByReader(reader);

    String[] messages = err.toString().split("\n");
    assertTrue(Arrays.stream(messages).allMatch(s -> s.contentEquals("There is no such reader!")));

  }

  @Test
  void shouldPrintThereIsNoSuchBookWhenOptionalBookEmpty(){
    Optional<Reader> reader = Optional.of(getReader());
    Optional<Book> book = Optional.empty();

    service.borrowBook(book, reader);
    service.returnBook(book, reader);
    service.getCurrentReaderOfBook(book);

    String[] messages = err.toString().split("\n");
    assertTrue(Arrays.stream(messages).allMatch(s -> s.contentEquals("There is no such book!")));
  }

  @Test
  void shouldPrintThatReaderCantBorrowBookIfItBorrowed() throws RegistryRepositoryException {
    when(repository.borrowBook(any(), any())).thenThrow(new RegistryRepositoryException("Book is already borrowed! You can't borrow it"));
    service.borrowBook(Optional.of(getBook()), Optional.of(getReader()));
    assertTrue(err.toString().contains("Book is already borrowed! You can't borrow it"));
  }

  @Test
  void shouldPrintThatReaderBorrowBookSuccessful() throws RegistryRepositoryException {
    when(repository.borrowBook(any(), any())).thenReturn(true);
    Reader reader = getReader();
    Book book = getBook();
    service.borrowBook(Optional.of(book), Optional.of(reader));
    String message = output.toString();
    assertAll(
        () -> assertTrue(message.contains(reader.getName())),
        () -> assertTrue(message.contains(book.getName())),
        () -> assertTrue(message.contains("borrow book"))
    );
  }

  @Test
  void shouldReturnEmptyListIfReaderDoesNotBorrowBook(){
    Reader reader = getReader();
    when(repository.getListBorrowedBooksOfReader(reader)).thenReturn(List.of());
    assertTrue(service.getAllBorrowedBooksByReader(Optional.of(reader)).isEmpty());
  }

  @Test
  void shouldReturnListOfReaderBorrowedBooks(){
    Reader reader = getReader();
    List<Book> books = setIdForTestBooks(getTestBooks());
    when(repository.getListBorrowedBooksOfReader(reader)).thenReturn(books);
    List<Book> listBorrowed = service.getAllBorrowedBooksByReader(Optional.of(reader));
    assertAll(() -> assertFalse(listBorrowed.isEmpty()),
              () -> assertEquals(3, listBorrowed.size()),
              () -> assertTrue(listBorrowed.containsAll(books)));
  }

  @Test
  void shouldReturnCurrentReaderOfBook(){
    when(repository.getReaderOfBook(getBook())).thenReturn(Optional.of(getReader()));
    Optional<Reader> reader = service.getCurrentReaderOfBook(Optional.of(getBook()));
    assertAll(() -> assertTrue(reader.isPresent()),
              () -> assertTrue(reader.get().getName().contentEquals("reader")));
  }

  @Test
  void shouldReturnEmptyOptional(){
      when(repository.getReaderOfBook(getBook())).thenReturn(Optional.empty());
      Optional<Reader> currentReaderOfBook = service.getCurrentReaderOfBook(Optional.of(getBook()));
      assertTrue(currentReaderOfBook.isEmpty());
  }

  @Test
  void shouldPrintThatReaderReturnBook() throws RegistryRepositoryException {
    when(repository.returnBook(any(), any())).thenReturn(true);
    Reader reader = getReader();
    Book book = getBook();
    service.returnBook(Optional.of(book), Optional.of(reader));
    String message = output.toString();
    assertAll(
        () -> assertTrue(message.contains(reader.getName())),
        () -> assertTrue(message.contains(book.getName())),
        () -> assertTrue(message.contains("return book"))
    );
  }

  @Test
  void shouldPrintThatReaderDoesntBorrowAnyBooks() throws RegistryRepositoryException {
    String message = "This reader doesn't borrow any book!";
    Reader reader = getReader();
    Book book = getBook();
    when(repository.returnBook(book,reader)).thenThrow(new RegistryRepositoryException(message));
    service.returnBook(Optional.of(book), Optional.of(reader));
    assertTrue(err.toString().contains(message));
  }

  @Test
  void shouldPrintReaderDoesntBorrowCurrentBook()
      throws RegistryRepositoryException {
    Reader reader = getReader();
    Book book = getBook();
    String message = "Reader " + reader.getName() + " didn't borrow " + book.getName();
      when(repository.returnBook(book, reader)).thenThrow(new RegistryRepositoryException(message));
      service.returnBook(Optional.of(book), Optional.of(reader));
      assertTrue(err.toString().contains(message));
  }

}