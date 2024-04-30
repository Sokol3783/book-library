package org.example.services;

import static org.example.util.Util.getBook;
import static org.example.util.Util.getReader;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RegistryServiceTest {

 private static RegistryRepository repository;
  private static RegistryService service;
  private static BookService bookService;
  private static ReaderService readerService;


  @BeforeEach
  void setUp() {
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
        () -> assertThrows(RuntimeException.class, () -> service.findBorrowedBooksByReader("1")));
  }

  @Test
  void shouldPrintThereIsNoSuchBookWhenOptionalBookEmpty() {
    when(readerService.findById(any())).thenReturn(Optional.of(new Reader(1l, "reader")));
    when(bookService.findById(any())).thenThrow(new RuntimeException("Book not found"));

    assertAll(() -> assertThrows(RuntimeException.class, () -> service.borrowBook("1 / 1")),
        () -> assertThrows(RuntimeException.class, () -> service.returnBook("5")),
        () -> assertThrows(RuntimeException.class, () -> service.findCurrentReaderOfBook(" 5 ")));

  }

  @Test
  void shouldPrintThatReaderCantBorrowBookIfItBorrowed() throws RegistryRepositoryException {
    when(readerService.findById(anyString())).thenReturn(Optional.of(getReader()));
    when(bookService.findById(anyString())).thenReturn(Optional.of(getBook()));
    doThrow(new RegistryRepositoryException("Book is already borrowed! You can't borrow it")).when(
        repository).borrowBook(any(), any());
    assertThrows(RegistryRepositoryException.class, () -> service.borrowBook("1 / 1"));
  }

  @Test
  void shouldPrintThatReaderBorrowBookSuccessful() throws RegistryRepositoryException {
    Reader reader = getReader();
    Book book = getBook();
    doNothing().when(repository).borrowBook(any(), any());
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    when(readerService.findById(anyString())).thenReturn(Optional.of(reader));
    Book borrowedBook= service.borrowBook("1 / 1");
    assertAll( () -> assertEquals(book, borrowedBook),
        () -> assertEquals("book", borrowedBook.getName()),
        () -> assertEquals("book", borrowedBook.getAuthor())
    );
  }

  @Test
  void shouldPrintListOfReaderBorrowedBooks() {
    Reader reader = getReader();
    List<Book> books = setIdForTestBooks(getTestBooks());

    when(repository.getListBorrowedBooksOfReader(reader)).thenReturn(books);
    when(readerService.findById(anyString())).thenReturn(Optional.of(reader));
    List<Book> borrowedBooks = service.findBorrowedBooksByReader("1");

    assertAll(() -> assertEquals(3, borrowedBooks.size()),
              () -> assertTrue(borrowedBooks.stream().allMatch(s -> s.getName().contains("Title"))),
              () -> assertTrue(borrowedBooks.stream().allMatch(s -> s.getAuthor().contains("Test"))));

  }

  @Test
  void shouldPrintCurrentReaderOfBook() {
    Book book = getBook();
    when(repository.getReaderOfBook(any())).thenReturn(Optional.of(getReader()));
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    Reader reader = service.findCurrentReaderOfBook("3");
    assertAll(() -> assertEquals(reader, getReader()),
        () -> assertEquals("reader", reader.getName()));
  }

  @Test
  void shouldPrintThatNobodyBorrowThisBook() {
    when(bookService.findById("1")).thenReturn(Optional.of(getBook()));
    when(repository.getReaderOfBook(getBook())).thenReturn(Optional.empty());
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> service.findCurrentReaderOfBook("1"));
    assertTrue(exception.getMessage().contains("Nobody reads this book"));
  }

  @Test
  void shouldPrintThatBookReturned() throws RegistryRepositoryException {
    Book book = getBook();
    doNothing().when(repository).returnBook(any());
    when(bookService.findById(any())).thenReturn(Optional.of(book));
    Book returnedBook = service.returnBook("1");

    assertAll(() -> assertEquals(book, returnedBook),
        () -> assertEquals("book", book.getAuthor()),
        () -> assertEquals("book", book.getName()));
  }

  @Test
  void shouldPrintThatAnybodyDoesNotBorrowAnyBooks() throws RegistryRepositoryException {
    String message = "Anybody doesn't borrow this book!";
    Book book = getBook();
    doThrow(new RegistryRepositoryException(message)).when(repository).returnBook(book);
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    RegistryRepositoryException exception = assertThrows(
        RegistryRepositoryException.class, () -> service.returnBook("1"));
    assertTrue(exception.getMessage().contains(message));
  }

}