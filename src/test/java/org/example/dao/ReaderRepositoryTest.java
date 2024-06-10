package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
import org.example.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReaderRepositoryTest {

  private final ReaderRepository readerRepository = new ReaderRepository();

  @BeforeAll
  static void setUpDB() {
    DBUtil.initDatabase();
  }

  @BeforeEach
  void setUp() throws SQLException {
    Util.executeSQLScript("readers.sql");
  }

  @Test
  void shouldFindById() {
    readerRepository.findById(1L).
        ifPresentOrElse(reader -> assertAll(
                () -> assertEquals(1L, reader.getId()),
                () -> assertEquals("Mike Douglas", reader.getName())
            ),
            Assertions::fail
        );
  }


  @ParameterizedTest
  @ValueSource(longs = {5L, 250L, 1000L, 12631231L})
  void shouldNotFindById(Long id) {
    assertTrue(readerRepository.findById(id).isEmpty());
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
    var existingReaderOne = afterStart.get(0);
    var existingReaderTwo = afterStart.get(1);
    var existingReaderThree = afterStart.get(2);
    assertAll(() -> assertEquals(1L, existingReaderOne.getId()),
        () -> assertEquals(2L, existingReaderTwo.getId()),
        () -> assertEquals(3L, existingReaderThree.getId()),
        () -> assertEquals("Mike Douglas", existingReaderOne.getName()),
        () -> assertEquals("Fedor Trybeckoi", existingReaderTwo.getName()),
        () -> assertEquals("IVAN MAZEPA", existingReaderThree.getName()));
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