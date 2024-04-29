package org.example.services;

import static org.example.util.Util.getBook;
import static org.example.util.Util.getReader;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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

  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private static RegistryRepository repository;
  private static RegistryService service;
  private static BookService bookService;
  private static ReaderService readerService;

  @BeforeAll
  static void setUpOutput() {
    System.setOut(new PrintStream(output));
    System.setErr(new PrintStream(err));
  }

  @AfterAll
  static void resetOutput() {
    System.setOut(System.out);
    System.setOut(System.err);
  }

  @BeforeEach
  void setUp() {
    output.reset();
    err.reset();
    repository = Mockito.mock(RegistryRepository.class);
    bookService = Mockito.mock(BookService.class);
    readerService = Mockito.mock(ReaderService.class);
    service = new RegistryService(repository, bookService, readerService);
  }

  @Test
  void shouldPrintThereIsNoSuchReaderWhenOptionalReaderEmpty() {
    when(readerService.findById(any())).thenThrow(new RuntimeException());
    when(bookService.findById(any())).thenReturn(Optional.of((new Book(1L, "book", "book"))));
    assertAll(() -> assertThrows(RuntimeException.class, () -> service.borrowBook("1 / 1")),
        () -> assertThrows(RuntimeException.class, () -> service.printBorrowedBooksByReader("1")));
  }

  @Test
  void shouldPrintThereIsNoSuchBookWhenOptionalBookEmpty() {
    when(readerService.findById(any())).thenReturn(Optional.of(new Reader(1l, "reader")));
    when(bookService.findById(any())).thenThrow(new RuntimeException("Book not found"));

    assertAll(() -> assertThrows(RuntimeException.class, () -> service.borrowBook("1 / 1")),
        () -> assertThrows(RuntimeException.class, () -> service.returnBook("5")),
        () -> assertThrows(RuntimeException.class, () -> service.printCurrentReaderOfBook(" 5 ")));

  }

  @Test
  void shouldPrintThatReaderCantBorrowBookIfItBorrowed() throws RegistryRepositoryException {
    when(repository.borrowBook(any(), any())).thenThrow(
        new RegistryRepositoryException("Book is already borrowed! You can't borrow it"));

    assertThrows(RegistryRepositoryException.class, () -> service.borrowBook("1 / 1"));
  }

  @Test
  void shouldPrintThatReaderBorrowBookSuccessful() throws RegistryRepositoryException {
    Reader reader = getReader();
    Book book = getBook();
    when(repository.borrowBook(any(), any())).thenReturn(true);
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    when(readerService.findById(anyString())).thenReturn(Optional.of(reader));
    String message = output.toString();
    service.borrowBook("1 / 1");
    assertAll(
        () -> assertTrue(message.contains(reader.getName())),
        () -> assertTrue(message.contains(book.getName())),
        () -> assertTrue(message.contains("borrow book"))
    );
  }

  @Test
  void shouldPrintListOfReaderBorrowedBooks() {
    Reader reader = getReader();
    List<Book> books = setIdForTestBooks(getTestBooks());

    when(repository.getListBorrowedBooksOfReader(reader)).thenReturn(books);
    when(readerService.findById(anyString())).thenReturn(Optional.of(reader));
    service.printBorrowedBooksByReader("1");

    String message = output.toString();
    assertAll(() -> assertTrue(message.contains("Borrowed books:")),
        () -> assertTrue(message.contains(books.get(0).toString())),
        () -> assertTrue(message.contains(books.get(1).toString())),
        () -> assertTrue(message.contains(books.get(2).toString())));
  }

  @Test
  void shouldPrintCurrentReaderOfBook() {
    Book book = getBook();
    when(repository.getReaderOfBook(any())).thenReturn(Optional.of(getReader()));
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    service.printCurrentReaderOfBook("3");
    String message = output.toString();
    assertTrue(message.contains("Book book read reader"));
  }

  @Test
  void shouldPrintThtNobody() {
    when(repository.getReaderOfBook(getBook())).thenReturn(Optional.empty());
    service.printCurrentReaderOfBook(" 1");
    String message = output.toString();
    assertTrue(message.contains("Nobody reads book!"));
  }

  @Test
  void shouldPrintThatBookReturned() throws RegistryRepositoryException {
    Book book = getBook();
    when(repository.returnBook(any())).thenReturn(true);
    when(bookService.findById(any())).thenReturn(Optional.of(book));
    String message = output.toString();
    assertAll(
        () -> assertTrue(message.contains(book.getName())),
        () -> assertTrue(message.contains("is returned"))
    );
  }

  @Test
  void shouldPrintThatAnybodyDoesNotBorrowAnyBooks() throws RegistryRepositoryException {
    String message = "Anybody doesn't borrow this book!";
    Book book = getBook();
    when(repository.returnBook(book)).thenThrow(new RegistryRepositoryException(message));
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    RegistryRepositoryException exception = assertThrows(
        RegistryRepositoryException.class, () -> service.returnBook("1"));
   assertTrue(exception.getMessage().contains(message));
  }

}