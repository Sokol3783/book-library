package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
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
    Optional<Reader> firstReader = repository.findById(1L);
    Reader reader = firstReader.orElse(new Reader(5120L, "dasd"));
    assertAll(() -> assertEquals(1L, reader.getId()),
        () -> assertEquals("Mike Douglas", reader.getName()));
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
  void shouldFindAllReaders() {
    List<Reader> afterStartup = repository.findAll();
    assertEquals(3, afterStartup.size());
    repository.save(new Reader(1L, "New Reader"));
    repository.save(new Reader(2L, "Second new"));
    List<Reader> all = repository.findAll();
    assertAll(() -> assertEquals(5, all.size()),
        () -> assertTrue(all.stream().anyMatch(s -> s.getId() == 4L)),
        () -> assertTrue(all.stream().anyMatch(s -> s.getId() == 5L)));
  }

  @Test
  void shouldHaveThreeReadersOnStartup() {
    List<Reader> afterStart = repository.findAll();
    Reader save = afterStart.get(0);
    Reader save1 = afterStart.get(1);
    Reader save2 = afterStart.get(2);
    assertAll(() -> assertEquals(1L, save.getId()),
        () -> assertEquals(2L, save1.getId()),
        () -> assertEquals(3L, save2.getId()),
        () -> assertEquals("Mike Douglas", save.getName()),
        () -> assertEquals("Fedor Trybeckoi", save1.getName()),
        () -> assertEquals("Ivan Mazepa", save2.getName()));
  }

}