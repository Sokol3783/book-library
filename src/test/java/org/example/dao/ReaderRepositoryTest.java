package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
import org.example.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReaderRepositoryTest {

  private final static ReaderRepository readerRepository = new ReaderRepository();

  @BeforeAll
  static void setUpDB() {
    DBUtil.initDatabase();
  }

  @BeforeEach
  void setUp() throws SQLException {
    var connection = DBUtil.getConnection();
    Util.executeSQLScript(connection, "readers.sql");
  }

  @Test
  void shouldFindById() {
    Optional<Reader> firstReader = readerRepository.findById(1L);
    Reader reader = firstReader.orElse(Util.getReaderWhenError());
    assertAll(() -> assertEquals(1L, reader.getId()),
        () -> assertEquals("Mike Douglas", reader.getName()));
  }


  @Test
  void shouldNotFindById() {
    assertAll(() -> assertTrue(readerRepository.findById(5L).isEmpty()),
        () -> assertTrue(readerRepository.findById(250L).isEmpty()),
        () -> assertTrue(readerRepository.findById(1000L).isEmpty()),
        () -> assertTrue(readerRepository.findById(12631231L).isEmpty()),
        () -> assertTrue(readerRepository.findById(1L).isPresent()));
  }

  @Test
  void shouldFindAllReaders() {
    List<Reader> afterStartup = readerRepository.findAll();
    assertEquals(3, afterStartup.size());
    readerRepository.save(new Reader("New Reader"));
    readerRepository.save(new Reader("Second new"));
    List<Reader> all = readerRepository.findAll();
    assertAll(() -> assertEquals(5, all.size()),
        () -> assertTrue(all.stream().anyMatch(s -> s.getId() == 4L)),
        () -> assertTrue(all.stream().anyMatch(s -> s.getId() == 5L)));
  }

  @Test
  void shouldHaveThreeReadersOnStartup() {
    List<Reader> afterStart = readerRepository.findAll();
    Reader save = afterStart.get(0);
    Reader save1 = afterStart.get(1);
    Reader save2 = afterStart.get(2);
    assertAll(() -> assertEquals(1L, save.getId()),
        () -> assertEquals(2L, save1.getId()),
        () -> assertEquals(3L, save2.getId()),
        () -> assertEquals("Mike Douglas", save.getName()),
        () -> assertEquals("Fedor Trybeckoi", save1.getName()),
        () -> assertEquals("IVAN MAZEPA", save2.getName()));
  }

  @Test
  void shouldSaveNewReaderWithIdFour() {
    var newReader = new Reader("test reader");
    readerRepository.save(newReader);
    var optionalReader = readerRepository.findById(4L);
    var allReader = readerRepository.findAll();
    assertAll(() -> assertTrue(optionalReader.isPresent()),
        () -> assertTrue(isNameEquals(optionalReader, newReader)),
        () -> assertEquals(4, allReader.size()));
  }

  private boolean isNameEquals(Optional<Reader> optionalReader, Reader newReader) {
    return optionalReader.map(reader -> reader.getName().contentEquals(newReader.getName()))
        .orElse(false);
  }
}