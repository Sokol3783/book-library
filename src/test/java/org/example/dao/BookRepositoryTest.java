package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.example.entity.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookRepositoryTest {

  private static BookRepository repository;

  @BeforeEach
  void setUp() {
    repository = new BookRepository();
  }

  @Test
  void shouldFindById() {
    Book bookID_1 = repository.findById(1L).get();
    Book bookID_3 = repository.findById(3L).get();
    assertAll(() -> assertEquals(1L, bookID_1.getId()),
        () -> assertEquals("The Dark Tower", bookID_1.getName()),
        () -> assertEquals("Steven King", bookID_1.getAuthor()),
        () -> assertEquals(3L, bookID_3.getId()),
        () -> assertEquals("A Game of Thrones", bookID_3.getName()),
        () -> assertEquals("George Martin", bookID_3.getAuthor()));
  }


  @Test
  void shouldNotFindById() {
    assertAll(() -> assertTrue(repository.findById(5L).isEmpty()),
        () -> assertTrue(repository.findById(250L).isEmpty()),
        () -> assertTrue(repository.findById(1000L).isEmpty()),
        () -> assertTrue(repository.findById(12631231L).isEmpty()),
        () -> assertTrue(repository.findById(1L).isPresent()));
  }

  @Test
  void shouldFindAllBooks() {
    List<Book> allOnStartup = repository.findAll();
    assertEquals(3, allOnStartup.size());
    repository.save(new Book(1L, "Book", "Book"));
    repository.save(new Book(1L, "Book2", "Book4"));

    List<Book> allAfterChanges = repository.findAll();
    assertAll(() -> assertEquals(5, allAfterChanges.size()),
        () -> assertTrue(allAfterChanges.stream().anyMatch( s ->s.getId() == 4L)),
        () -> assertTrue(allAfterChanges.stream().anyMatch( s ->s.getId() == 5L)));

  }

  @Test
  void shouldHaveThreeBooksOnStartup() {
    List<Book> afterStartup = repository.findAll();
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

}