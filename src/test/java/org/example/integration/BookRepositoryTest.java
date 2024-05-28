package org.example.integration;

import static org.example.util.Util.getBookWhenError;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.example.dao.BookRepository;
import org.example.dao.DBUtil;
import org.example.entity.Book;
import org.example.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookRepositoryTest {

  private static final BookRepository bookRepository = new BookRepository();

  @BeforeAll
  static void setUpClass() {
    DBUtil.initDatabase();
  }


  private static boolean titleIsEquals(Optional<Book> optionalBook, Book newBook) {
    return optionalBook.map(savedBook -> savedBook.getName().contentEquals(newBook.getName()))
        .orElse(false);
  }

  @Test
  void shouldNotFindById() {
    assertAll(() -> assertTrue(bookRepository.findById(5L).isEmpty()),
        () -> assertTrue(bookRepository.findById(250L).isEmpty()),
        () -> assertTrue(bookRepository.findById(1000L).isEmpty()),
        () -> assertTrue(bookRepository.findById(12631231L).isEmpty()),
        () -> assertTrue(bookRepository.findById(1L).isPresent()));
  }

  @Test
  void shouldFindAllBooks() {
    List<Book> allOnStartup = bookRepository.findAll();
    assertEquals(3, allOnStartup.size());
    bookRepository.save(new Book("Book", "Book"));
    bookRepository.save(new Book("Book2", "Book4"));

    List<Book> allAfterChanges = bookRepository.findAll();
    assertAll(() -> assertEquals(5, allAfterChanges.size()),
        () -> assertTrue(allAfterChanges.stream().anyMatch(s -> s.getId() == 4L)),
        () -> assertTrue(allAfterChanges.stream().anyMatch(s -> s.getId() == 5L)));
  }

  @Test
  void shouldHaveThreeBooksOnStartup() {
    List<Book> afterStartup = bookRepository.findAll();
    Book save = afterStartup.get(0);
    Book save1 = afterStartup.get(1);
    Book save2 = afterStartup.get(2);

    assertAll(() -> assertEquals(1L, save.getId()),
        () -> assertEquals(2L, save1.getId()),
        () -> assertEquals(3L, save2.getId()),
        () -> assertEquals("Steven King", save.getAuthor()),
        () -> assertEquals("Patric Rotfuss", save1.getAuthor()),
        () -> assertEquals("George Martin", save2.getAuthor()),
        () -> assertEquals("The Dark Tower", save.getName()),
        () -> assertEquals("The name of the Wind", save1.getName()),
        () -> assertEquals("A Game of Thrones", save2.getName()));
  }

  @BeforeEach
  void setUp() throws SQLException {
    Connection connection = DBUtil.getConnection();
    Util.executeSQLScript(connection, "books.sql");
  }

  @Test
  void shouldSaveNewBookWithId4() {
    var newBook = new Book("new book", "new book author");
    bookRepository.save(newBook);

    var optionalBook = bookRepository.findById(4L);
    var listOfBooks = bookRepository.findAll();

    assertAll(() -> assertTrue(optionalBook.isPresent()),
        () -> assertTrue(titleIsEquals(optionalBook, newBook)),
        () -> assertTrue(authorIsEquals(optionalBook, newBook)),
        () -> assertEquals(4, listOfBooks.size()));
  }

  @Test
  void shouldFindById() {
    Book bookID_1 = bookRepository.findById(1L).orElse(getBookWhenError());
    Book bookID_3 = bookRepository.findById(3L).orElse(getBookWhenError());
    assertAll(() -> assertEquals(1L, bookID_1.getId()),
        () -> assertEquals("The Dark Tower", bookID_1.getName()),
        () -> assertEquals("Steven King", bookID_1.getAuthor()),
        () -> assertEquals(3L, bookID_3.getId()),
        () -> assertEquals("A Game of Thrones", bookID_3.getName()),
        () -> assertEquals("George Martin", bookID_3.getAuthor()));
  }

  private boolean authorIsEquals(Optional<Book> optionalBook, Book newBook) {
    return optionalBook.map(savedBook -> savedBook.getAuthor().contentEquals(newBook.getAuthor()))
        .orElse(false);
  }
}