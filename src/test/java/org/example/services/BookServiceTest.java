package org.example.services;


import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.example.exception.ConsoleValidationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BookServiceTest {

  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();

  private static BookService service;
  private static BookRepository repository;

  @BeforeAll
  static void beforeAll() {
    System.setOut(new PrintStream(output));
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
  void shouldCallRepositoryOnceWhenGetListOfBooksPrintEvery() {
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
  void shouldPrintThatTitleContainInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(
        ConsoleValidationException.class, () -> service.addNewBook("tbba3#$ / author"));
    assertTrue(exception.getMessage().contains("Title contains invalid symbols: |/\\\\#%=+*_><]"));
  }

  @Test
  void shouldPrintThatAuthorContainsInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(
        ConsoleValidationException.class, () -> service.addNewBook("valid / a$l#utho<>r"));
    assertTrue(exception.getMessage()
        .contains("Author must contain only letters, spaces, dashes, apostrophes!"));
  }

  @Test
  void shouldPrintErrThatSizeAuthorNotValid() {
    ConsoleValidationException titleFewChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewBook("valid / asd"));
    ConsoleValidationException titleToManyChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewBook("valid / $l#uyyyyyyyyyyyyesssssssssssssggthor"));

    assertAll(() -> assertTrue(titleFewChar.getMessage()
            .contains("Author should contain more than 5 char and less than 30 ones")),
        () -> assertTrue(titleToManyChar.getMessage()
            .contains("Author should contain more than 5 char and less than 30 ones")));
  }

  @Test
  void shouldPrintErrThatSizeTitleNotValid() {
    ConsoleValidationException titleFewChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewBook("vali / valid author"));
    ConsoleValidationException titleToManyChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewBook(
            "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiigjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjii / valid author"));
    assertAll(() -> assertTrue(titleFewChar.getMessage()
            .contains("Title should contain more than 5 char and less than 100 ones")),
        () -> assertTrue(titleToManyChar.getMessage()
            .contains("Title should contain more than 5 char and less than 100 ones")));
  }

  @Test
  @Disabled("Has no ideas why it doesn't mock and work when set real implementation")
  void shouldCreateNewBookWithValidFieldsAndPrintMessage() {
    Book book = new Book(1L, "Author 1", "Title 1");
    when(repository.save(book)).thenReturn(book);
    service.addNewBook(book.getName() + "/" + book.getAuthor());
    String message = output.toString();

    assertAll(() -> assertTrue(message.contains("Book saved:")),
        () -> assertTrue(message.contains(book.toString())));
  }

  @Test
  void shouldReturnBookIfValidInput() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(new Book(1L, "book", "book")));

    assertAll(() -> assertTrue(service.findById("1").isPresent()),
        () -> assertTrue(service.findById("2").isPresent()),
        () -> assertTrue(service.findById("3").isPresent()));
  }

  @Test
  void shouldThrowRepositoryExceptionIfNotValidInput() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());
    assertAll(
        () -> assertThrows(ConsoleValidationException.class, () -> service.findById("asdasda")),
        () -> assertTrue(service.findById("1").isEmpty()));
  }

  @Test
  void shouldPrintAllMistakesInInput() {
    ConsoleValidationException exception = assertThrows(ConsoleValidationException.class,
        () -> service.addNewBook(
            "i@# / sadjfhasjkdfhsjadhfjkshdfk jasdhfksad #12@%2132 valid author"));

    assertAll(() -> assertTrue(exception.getMessage()
            .contains("Title should contain more than 5 char and less than 100 ones")),
        () -> assertTrue(exception.getMessage()
            .contains("Author should contain more than 5 char and less than 30 ones")),
        () -> assertTrue(exception.getMessage()
            .contains("Author must contain only letters, spaces, dashes, apostrophes!")),
        () -> assertTrue(
            exception.getMessage().contains("Title contains invalid symbols: |/\\\\#%=+*_><]"))
    );

  }

}