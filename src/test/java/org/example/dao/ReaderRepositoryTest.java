package org.example.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.example.entity.ReaderEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReaderRepositoryTest {

  private static ReaderRepository repository = ReaderRepository.getInstance();

  @BeforeEach
  void setUp() {
    Collection<ReaderEntity> all = getDefaultReaderEntity();
    all.forEach(s -> repository.save(s));
  }

  @AfterEach
  void tearDown() {
    Collection<ReaderEntity> all = repository.findAll();
    all.forEach(s -> repository.deleteById(s.getId()));
  }

  private Collection<ReaderEntity> getDefaultReaderEntity() {
    return List.of(new ReaderEntity(1, "Reader 1"),
        new ReaderEntity(2, "Reader 2"),
        new ReaderEntity(3, "Reader 3"));
  }

  @Test
  void findById() {
    Optional<ReaderEntity> optional = repository.findById(1l);

    assertTrue(optional.isPresent());
    ReaderEntity book = optional.get();
    assertEquals(1l, book.getId());
    assertEquals("Reader 1", book.getName());

  }

  @Test
  void whenNotFoundById(){
    Optional<ReaderEntity> optional = repository.findById(20l);
    assertTrue(optional.isEmpty());
  }

  @Test
  void save() {
    ReaderEntity entity = new ReaderEntity(0, "Reader 4");
    ReaderEntity copy = copyReaderEntity(entity);
    Optional<ReaderEntity> optionalSaved = repository.save(entity);
    assertTrue(optionalSaved.isPresent());
    ReaderEntity saved = optionalSaved.get();
    assertNotEquals(0l, saved.getId());
    assertEquals(entity, saved);
    assertNotEquals(copy, saved);
    assertEquals(copy.getName(), saved.getName());

  }

  private ReaderEntity copyReaderEntity(ReaderEntity entity) {
    return new ReaderEntity(entity.getId(), entity.getName());
  }

  @Test
  void update() {
    Optional<ReaderEntity> byId = repository.findById(1l);
    assertTrue(byId.isPresent());
    ReaderEntity entity = byId.get();
    ReaderEntity oldValue = copyReaderEntity(entity);
    entity.setName("new name");
    Optional<ReaderEntity> optionalSaved = repository.update(entity);
    assertTrue(optionalSaved.isPresent());
    ReaderEntity saved = optionalSaved.get();
    assertEquals(entity, saved);
    assertNotEquals(oldValue.getName(), saved.getName());
  }

  @Test
  void deleteByIdReturnFalseWhenNoData() {
    Collection<ReaderEntity> all = repository.findAll();
    assertFalse(repository.deleteById(0l));
    assertEquals(all, repository.findAll());
  }

  @Test
  void deleteTwiceByTheSameId() {
    Collection<ReaderEntity> all = repository.findAll();
    assertTrue(() -> repository.deleteById(1l));
    Collection<ReaderEntity> afterDelete = repository.findAll();
    assertTrue(all.size() > afterDelete.size());
    assertNotEquals(all, afterDelete);
    assertFalse(() -> repository.deleteById(1l));
    assertEquals(afterDelete, repository.findAll());
  }


  @Test
  void findAllAfterStartup() {
    Collection<ReaderEntity> all = repository.findAll();
    assertNotEquals(0, all.size());
  }

  @Test
  void findAllAfterAddOne() {
    Collection<ReaderEntity> all = repository.findAll();
    assertNotEquals(0, all.size());
    int size = all.size();
    repository.save(new ReaderEntity(0, "name"));
    Collection<ReaderEntity> allAfterAdd = repository.findAll();
    assertNotEquals(size, allAfterAdd.size());
    assertNotEquals(allAfterAdd, all);
  }

}