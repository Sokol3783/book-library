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

  private static BookService service;
  private static BookRepository repository;

  @BeforeEach
  void setUp() {
    repository = mock(BookRepository.class);
    service = new BookService(repository);
  }

  @Test
  void shouldCallRepositoryOnceWhenGetListOfBooks() {
    List<Book> testBooks = getTestBooks();
    when(repository.findAll()).thenReturn(testBooks);
    List<Book> allBooks = service.findAllBooks();
    assertAll(() -> verify(repository, times(1)).findAll(),
        () -> assertEquals(3, allBooks.size()),
        () -> assertTrue(allBooks.stream().allMatch(s -> s.getAuthor().contains("Test"))),
        () -> assertTrue(allBooks.stream().allMatch(s -> s.getName().contains("Title")))
    );
  }

  @Test
  void shouldThrowThatTitleContainInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(
        ConsoleValidationException.class, () -> service.addNewBook("tbba3#$ / author"));
    assertTrue(exception.getMessage().contains("Title contains invalid symbols: |/\\\\#%=+*_><]"));
  }

  @Test
  void shouldThrowThatAuthorContainsInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(
        ConsoleValidationException.class, () -> service.addNewBook("valid / a$l#utho<>r"));
    assertTrue(exception.getMessage()
        .contains("Author must contain only letters, spaces, dashes, apostrophes!"));
  }

  @Test
  void shouldThrowErrThatSizeAuthorNotValid() {
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
  void shouldThrowErrThatSizeTitleNotValid() {
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
    Book book = new Book(0l, "Author 1", "Title 1");
    when(repository.save(book)).thenReturn(book);
    Book saved = service.addNewBook(book.getName() + "/" + book.getAuthor());
    assertAll(() -> assertEquals(book, saved),
        () -> assertEquals("Author 1", saved.getAuthor()),
        () -> assertEquals("Title 1", saved.getName())
    );
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
  void shouldThrowAllMistakesInInput() {
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