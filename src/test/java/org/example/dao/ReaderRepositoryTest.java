package org.example.dao;

import static org.example.util.Util.getTestReaders;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
import org.example.util.Util.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReaderRepositoryTest {

  private static ReaderRepository repository;

  @BeforeEach
  void setUp() {
    repository = new ReaderRepository();
  }

  @Test
  void shouldFindById() {
    saveToRepository();
    Optional<Reader> firstReader = repository.findById(1L);
    Reader reader = firstReader.orElse(new Reader(5120L, "dasd"));
    assertAll( () -> assertEquals(1L, reader.getId()),
        () -> assertEquals("Test 1", reader.getName()));
  }


  @Test
  void shouldNotFindById(){
    saveToRepository();
    assertAll(() -> assertTrue(repository.findById(5L).isEmpty()),
        () -> assertTrue(repository.findById(250L).isEmpty()),
        () -> assertTrue(repository.findById(1000L).isEmpty()),
        () -> assertTrue(repository.findById(12631231L).isEmpty()),
        () -> assertTrue(repository.findById(1L).isPresent()));
  }

  @Test
  void shouldFindAllReaders() {
    List<Reader> all = repository.findAll();
    assertTrue(all.isEmpty());
    saveToRepository();
    IdGenerator generator = new IdGenerator();
    List<Reader> testReaders = getTestReaders();
    testReaders.forEach(s -> s.setId(generator.getNextId()));
    List<Reader> allAfterSave = repository.findAll();
    assertAll(() -> assertFalse(allAfterSave.isEmpty(), "After saving test data repository shouldn't be empty"), () ->  assertTrue(allAfterSave.containsAll(testReaders), "After saving repository should contain all test data"),
        () -> assertEquals(3, allAfterSave.size()));
  }

  @Test
  void shouldSaveReaders() {
    Reader save = repository.save(new Reader(0, "name"));
    Reader save1 = repository.save(new Reader(0, "name2" ));
    Reader save2 = repository.save(new Reader(0,"name3"));

    assertAll(() -> assertEquals(1L,save.getId()),
        () -> assertEquals(2L, save1.getId()),
        () -> assertEquals(3L, save2.getId()),
        () -> assertEquals("name", save.getName()),
        () -> assertEquals("name2", save1.getName()),
        () -> assertEquals("name3", save2.getName()));
  }

  private void saveToRepository() {
    for(Reader reader : getTestReaders()) {
      repository.save(reader);
    }
  }
}