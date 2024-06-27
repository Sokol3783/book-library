package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import org.example.entity.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BookRepositoryTest {

  @Autowired
  private BookRepository bookRepository;

  @Test
  void shouldFindAllBooks() {
    List<Book> allOnStartup = bookRepository.findAll();
    assertEquals(3, allOnStartup.size());
  }

  @ParameterizedTest
  @ValueSource(longs = {5L, 250L, 1000L, 12631231L})
  void shouldNotFindById(Long id) {
    assertTrue(bookRepository.findById(id).isEmpty());
  }

  @Test
  @Transactional
  @Rollback
  @DisplayName("Should save book with id more than three, and find book by id with the same content")
  void shouldSaveNewBookWithIdMoreThanThree() {
    var newBook = new Book("new book", "new book author");
    var savedBook = bookRepository.save(newBook);

    var optionalBook = bookRepository.findById(4L);
    var listOfBooks = bookRepository.findAll();

    optionalBook.ifPresentOrElse(findByIdBook -> assertAll(
            () -> assertEquals(savedBook, findByIdBook),
            () -> assertTrue(titleIsEquals(findByIdBook, newBook)),
            () -> assertTrue(authorIsEquals(findByIdBook, newBook)),
            () -> assertTrue(findByIdBook.getId() > 3),
            () -> assertEquals(4, listOfBooks.size()))
        , fail()
    );
  }

  private boolean titleIsEquals(Book savedBook, Book newBook) {
    return savedBook.getName().contentEquals(newBook.getName());
  }

  private boolean authorIsEquals(Book savedBook, Book newBook) {
    return savedBook.getAuthor().contentEquals(newBook.getAuthor());
  }

  @Test
  void shouldHaveThreeBooksOnStartup() {
    List<Book> afterStartup = bookRepository.findAll();
    var existingBookOne = afterStartup.get(0);
    var existingBookTwo = afterStartup.get(1);
    var existingBookThree = afterStartup.get(2);

    assertAll(() -> assertEquals(3, afterStartup.size()),
        () -> assertEquals(1L, existingBookOne.getId()),
        () -> assertEquals(2L, existingBookTwo.getId()),
        () -> assertEquals(3L, existingBookThree.getId()),
        () -> assertEquals("Steven King", existingBookOne.getAuthor()),
        () -> assertEquals("Patric Rotfuss", existingBookTwo.getAuthor()),
        () -> assertEquals("George Martin", existingBookThree.getAuthor()),
        () -> assertEquals("The Dark Tower", existingBookOne.getName()),
        () -> assertEquals("The name of the Wind", existingBookTwo.getName()),
        () -> assertEquals("A Game of Thrones", existingBookThree.getName()));
  }

  @Test
  void shouldFindById() {
    bookRepository.findById(1L)
        .ifPresentOrElse(
            book -> assertAll(
                () -> assertEquals(1L, book.getId()),
                () -> assertEquals("The Dark Tower", book.getName()),
                () -> assertEquals("Steven King", book.getAuthor())
            ),
            Assertions::fail
        );
    bookRepository.findById(3L)
        .ifPresentOrElse(
            book -> assertAll(
                () -> assertEquals(3L, book.getId()),
                () -> assertEquals("A Game of Thrones", book.getName()),
                () -> assertEquals("George Martin", book.getAuthor())
            ),
            Assertions::fail
        );
  }

}