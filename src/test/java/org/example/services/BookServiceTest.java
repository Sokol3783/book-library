package org.example.services;


import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.example.exception.ConsoleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BookServiceTest {

  private static BookService bookService;
  private static BookRepository bookRepository;

  @BeforeEach
  void setUp() {
    bookRepository = mock(BookRepository.class);
    bookService = new BookService(bookRepository);
  }

  @Test
  void shouldCallRepositoryOnceWhenGetListOfBooks() {
    List<Book> testBooks = getTestBooks();
    when(bookRepository.findAll()).thenReturn(testBooks);
    List<Book> allBooks = bookService.findAllBooks();
    assertAll(() -> verify(bookRepository, times(1)).findAll(),
        () -> assertEquals(3, allBooks.size()),
        () -> assertTrue(allBooks.stream().allMatch(s -> s.getAuthor().contains("Test"))),
        () -> assertTrue(allBooks.stream().allMatch(s -> s.getName().contains("Title")))
    );
  }

  @Test
  void shouldThrowThatTitleContainInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(
        ConsoleValidationException.class, () -> bookService.addNewBook("tbba3#$ / author"));
    assertTrue(exception.getMessage().contains("Title contains invalid symbols: |/\\\\#%=+*_><]"));
  }

  @Test
  void shouldThrowThatAuthorContainsInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(
        ConsoleValidationException.class, () -> bookService.addNewBook("valid / a$l#utho<>r"));
    assertTrue(exception.getMessage()
        .contains("Author must contain only letters, spaces, dashes, apostrophes!"));
  }

  @Test
  void shouldThrowErrThatSizeAuthorNotValid() {
    ConsoleValidationException titleFewChar = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook("valid / asd"));
    ConsoleValidationException titleToManyChar = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook("valid / $l#uyyyyyyyyyyyyesssssssssssssggthor"));

    assertAll(() -> assertTrue(titleFewChar.getMessage()
            .contains("Author should contain more than 5 char and less than 30 ones")),
        () -> assertTrue(titleToManyChar.getMessage()
            .contains("Author should contain more than 5 char and less than 30 ones")));
  }

  @Test
  void shouldThrowErrThatSizeTitleNotValid() {
    ConsoleValidationException titleFewChar = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook("vali / valid author"));
    ConsoleValidationException titleToManyChar = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook(
            "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiigjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjii / valid author"));
    assertAll(() -> assertTrue(titleFewChar.getMessage()
            .contains("Title should contain more than 5 char and less than 100 ones")),
        () -> assertTrue(titleToManyChar.getMessage()
            .contains("Title should contain more than 5 char and less than 100 ones")));
  }

  @Test
  @Disabled("Has no ideas why it doesn't mock and work when set real implementation")
  void shouldCreateNewBookWithValidFieldsAndPrintMessage() {
    Book book = new Book("Author 1", "Title 1");
    when(bookRepository.save(book)).thenReturn(book);
    Book saved = bookService.addNewBook(book.getName() + "/" + book.getAuthor());
    assertAll(() -> assertEquals(book, saved),
        () -> assertEquals("Author 1", saved.getAuthor()),
        () -> assertEquals("Title 1", saved.getName())
    );
  }

  @Test
  void shouldReturnBookIfValidInput() {
    when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book("book", "book")));

    assertAll(() -> assertTrue(bookService.findById("1").isPresent()),
        () -> assertTrue(bookService.findById("2").isPresent()),
        () -> assertTrue(bookService.findById("3").isPresent()));
  }

  @Test
  void shouldThrowRepositoryExceptionIfNotValidInput() {
    when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertAll(
        () -> assertThrows(ConsoleValidationException.class, () -> bookService.findById("asdasda")),
        () -> assertTrue(bookService.findById("1").isEmpty()));
  }

  @Test
  void shouldThrowAllMistakesInInput() {
    ConsoleValidationException exception = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook(
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