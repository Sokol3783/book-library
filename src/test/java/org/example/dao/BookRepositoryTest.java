package org.example.dao;

import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
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
    saveToRepository();
    Optional<Book> firstBook = repository.findById(1L);
    Book book = firstBook.orElse(new Book(5120L, "dasd", "DASDAS"));
    assertAll( () -> assertEquals(1L, book.getId()),
        () -> assertEquals("Title 1", book.getName()),
        () -> assertEquals("Test 1", book.getAuthor()));
  }


  @Test
  void shouldNotFindById(){
    saveToRepository();
    assertAll(() -> assertTrue(repository.findById(5L).isEmpty()),
        () -> assertTrue(repository.findById(250L).isEmpty()),
        () -> assertTrue(repository.findById(1000L).isEmpty()),
        () -> assertTrue(repository.findById(12631231L).isEmpty()));
  }

  @Test
  void shouldFindAllBooks() {
    List<Book> all = repository.findAll();
    assertTrue(all.isEmpty());
    saveToRepository();
    IdGenerator generator = new IdGenerator();
    List<Book> testBooks = getTestBooks();
    testBooks.stream().forEach(s -> s.setId(generator.getNextId()));
    List<Book> allAfterSave = repository.findAll();
    assertAll(() -> assertFalse(allAfterSave.isEmpty(), "After saving test data repository shouldn't be empty"),
              () ->  assertTrue(allAfterSave.containsAll(testBooks), "After saving repository should contain all test data"),
              () -> assertEquals(3, allAfterSave.size()));
  }

  @Test
  void saveBook() {
    Book save = repository.save(new Book(0, "title", "name"));
    Book save1 = repository.save(new Book(0, "title2", "name2" ));
    Book save2 = repository.save(new Book(0,"title4","name"));

    assertAll(() -> assertEquals(1L,save.getId()),
        () -> assertEquals(2L, save1.getId()),
        () -> assertEquals(3L, save2.getId()),
        () -> assertEquals("name", save.getAuthor()),
        () -> assertEquals("name2", save1.getAuthor()),
        () -> assertEquals("name", save2.getAuthor()),
        () -> assertEquals("title", save.getName()),
        () -> assertEquals("title2", save1.getName()),
        () -> assertEquals("title4", save2.getName()));
  }

  private void saveToRepository() {
    for(Book book : getTestBooks()) {
     repository.save(book);
    }
  }

  class IdGenerator {
    private long id = 0;

    long getNextId() {
      return ++id;
    };

  }

}