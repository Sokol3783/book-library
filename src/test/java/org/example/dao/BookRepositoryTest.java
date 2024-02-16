package org.example.dao;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.example.entity.BookEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookRepositoryTest {

  private static BookRepository repository = BookRepository.getInstance();

  @BeforeEach
  void setUp() {
    Collection<BookEntity> all = getDefaultBookEntity();
    all.forEach(s -> repository.save(s));
  }

  @AfterEach
  void tearDown() {
    Collection<BookEntity> all = repository.findAll();
    all.forEach(s -> repository.deleteById(s.getId()));
  }

  private Collection<BookEntity> getDefaultBookEntity() {
    return List.of(new BookEntity(1, "Test book1", "Test author1"),
        new BookEntity(2, "Test book2", "Test author1"),
        new BookEntity(3, "Test book3", "Test author1"));
  }

  @Test
  void findById() {
    Optional<BookEntity> optional = repository.findById(1l);

    assertTrue(optional.isPresent());
    BookEntity book = optional.get();
    assertEquals(1l, book.getId());
    assertEquals("Test book1", book.getName());
    assertEquals("Test author1", book.getAuthor());

  }

  @Test
  void whenNotFoundById(){
    Optional<BookEntity> optional = repository.findById(20l);
    assertTrue(optional.isEmpty());
  }

  @Test
  void save() {
    BookEntity entity = new BookEntity(0, "new book", "new author");
    BookEntity copy = copyBookEntity(entity);
    Optional<BookEntity> optionalSaved = repository.save(entity);
    assertTrue(optionalSaved.isPresent());
    BookEntity saved = optionalSaved.get();
    assertNotEquals(0l, saved.getId());
    assertEquals(entity, saved);
    assertNotEquals(copy, saved);
    assertEquals(copy.getAuthor(), saved.getAuthor());
    assertEquals(copy.getName(), saved.getAuthor());

  }

  private BookEntity copyBookEntity(BookEntity entity) {
    return new BookEntity(entity.getId(), entity.getName(), entity.getAuthor());
  }

  @Test
  void update() {
    Optional<BookEntity> byId = repository.findById(1l);
    assertTrue(byId.isPresent());
    BookEntity entity = byId.get();
    BookEntity oldValue = copyBookEntity(entity);
    entity.setAuthor("new author");
    entity.setName("new name");
    Optional<BookEntity> optionalSaved = repository.update(entity);
    assertTrue(optionalSaved.isPresent());
    BookEntity saved = optionalSaved.get();
    assertEquals(entity, saved);
    assertNotEquals(oldValue.getAuthor(), saved.getAuthor());
    assertNotEquals(oldValue.getName(), saved.getName());
  }

  @Test
  void deleteById() {
    Collection<BookEntity> all = repository.findAll();
    assertDoesNotThrow(() -> repository.deleteById(0l), "Throw exception when not delete data!");
    assertEquals(all, repository.findAll());
    assertDoesNotThrow(() -> repository.deleteById(1l), "Throw exception when not delete data!");
    Collection<BookEntity> afterDelete = repository.findAll();
    assertTrue(all.size() > afterDelete.size());
    assertNotEquals(all, afterDelete);
    assertDoesNotThrow(() -> repository.deleteById(1l), "Throw exception when not delete data!");
    assertEquals(afterDelete, repository.findAll());
  }

  @Test
  void findAllAfterStartup() {
    Collection<BookEntity> all = repository.findAll();
    assertNotEquals(0, all.size());
  }

  @Test
  void findAllAfterAddOne() {
    Collection<BookEntity> all = repository.findAll();
    assertNotEquals(0, all.size());
    int size = all.size();
    repository.save(new BookEntity(0, "name", "author"));
    Collection<BookEntity> allAfterAdd = repository.findAll();
    assertNotEquals(size, allAfterAdd.size());
    assertNotEquals(allAfterAdd, all);
  }

}