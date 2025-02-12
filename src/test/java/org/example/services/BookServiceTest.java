package org.example.services;


import static org.example.util.Util.getFirstBook;
import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.example.exception.ConsoleValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class BookServiceTest {

  @Mock
  private BookRepository bookRepository;
  @InjectMocks
  private BookService bookService;

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

  @ParameterizedTest
  @MethodSource("provideInvalidAuthorsWithExpectedErrorMessage")
  void shouldThrowErrorWithMessageInvalidAuthor(String author, String message) {
    var exception = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook("title / " + author));
    assertTrue(exception.getMessage().contains(message));
  }

  private static Stream<Arguments> provideInvalidAuthorsWithExpectedErrorMessage() {
    return Stream.of(
        Arguments.of("asd", "Name should contain more than 5 chars and less than 30 ones"),
        Arguments.of("fashdfjsafsdfaskldfsdfsadfasdfhsjkdfhaskjdfhskjd",
            "Name should contain more than 5 chars and less than 30 ones"),
        Arguments.of("name!", "Name must contain only letters, spaces, dashes, apostrophes!"),
        Arguments.of("name//%&^*^&",
            "Name must contain only letters, spaces, dashes, apostrophes!"),
        Arguments.of("name&!@#^&", "Name must contain only letters, spaces, dashes, apostrophes!")
    );
  }


  @ParameterizedTest
  @MethodSource("provideInvalidTitleWithExpectedErrorMessage")
  void shouldThrowErrorWithMessageInvalidTitle(String title, String message) {
    var exception = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook(title + "/ author"));
    assertTrue(exception.getMessage().contains(message));
  }

  private static Stream<Arguments> provideInvalidTitleWithExpectedErrorMessage() {
    return Stream.of(
        Arguments.of("asd", "Title should contain more than 5 chars and less than 100 ones"),
        Arguments.of(
            "fashdgasdkjhfjkasdhdfjksahdfkjsahdfjmanvamshfklsdahfjaskdhfkalsjndfmasdnfjksadhfkasjdhfaskjdhfjsadhfkasjhdfknsnsmdfasfjsafsdfaskldfsdfsadfasdfhsjkdfhaskjdfhskjd",
            "Title should contain more than 5 chars and less than 100 ones"),
        Arguments.of("title+", "Title contains invalid symbols: |/\\\\#%=+*_><]"),
        Arguments.of("title*|", "Title contains invalid symbols: |/\\\\#%=+*_><]"),
        Arguments.of("title#^&", "Title contains invalid symbols: |/\\\\#%=+*_><]")
    );
  }

  @Test
  void shouldCreateNewBookWithValidFieldsAndReturnSavedBook() {
    var book = new Book("Title 1", "Author 1");
    when(bookRepository.save(book)).thenReturn(book);
    var saved = bookService.addNewBook(book.getName() + "/" + book.getAuthor());
    assertAll(() -> assertEquals(book, saved),
        () -> assertEquals("Author 1", saved.getAuthor()),
        () -> assertEquals("Title 1", saved.getName())
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "2", "3"})
  void shouldReturnBookIfValidInput(String id) {
    when(bookRepository.findById(anyLong())).thenReturn(Optional.of(new Book("book", "book")));
    assertTrue(bookService.findById(id).isPresent());
  }

  @Test
  void shouldThrowRepositoryExceptionIfNotValidInput() {
    assertThrows(ConsoleValidationException.class, () -> bookService.findById("asdasda"));
  }

  @Test
  void shouldThrowAllMistakesInInput() {
    var exception = assertThrows(ConsoleValidationException.class,
        () -> bookService.addNewBook(
            "i@# / sadjfhasjkdfhsjadhfjkshdfk jasdhfksad #12@%2132 valid author"));
    assertAll(() -> assertTrue(exception.getMessage()
            .contains("Title should contain more than 5 chars and less than 100 ones")),
        () -> assertTrue(exception.getMessage()
            .contains("Name should contain more than 5 chars and less than 30 ones")),
        () -> assertTrue(exception.getMessage()
            .contains("Name must contain only letters, spaces, dashes, apostrophes!")),
        () -> assertTrue(
            exception.getMessage().contains("Title contains invalid symbols: |/\\\\#%=+*_><]"))
    );
  }

  @Test
  void shouldSaveNewBookFromEntity() {
    when(bookRepository.save(any(Book.class))).thenReturn(getFirstBook());
    var book = new Book("book", "book");
    var saved = bookService.addNewBook(book);
    assertNotEquals(0, saved.getId());
    verify(bookRepository, times(1)).save(book);
  }

  @Test
  @DisplayName("Should find book by id when type is long and book present")
  void shouldFindByIdLong() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(getFirstBook()));
    assertTrue(bookService.findById(1L).isPresent());
    verify(bookRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName("Should return empty optional when value not present")
  void shouldNotFindByIdLong() {
    when(bookRepository.findById(500L)).thenReturn(Optional.empty());
    assertTrue(bookService.findById(500L).isEmpty());
    verify(bookRepository, times(1)).findById(anyLong());
  }

}