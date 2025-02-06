package org.example.dao;

import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({ReaderRepository.class})
class ReaderRepositoryTest {

  @Autowired
  private ReaderRepository readerRepository;

  @ParameterizedTest
  @CsvSource({"1,Mike Douglas", "2,Fedor Trybeckoi", "3,IVAN MAZEPA"})
  void shouldFindById(Long id, String name) {
    readerRepository.findById(id).
        ifPresentOrElse(reader -> assertAll(
                () -> assertEquals(id, reader.getId()),
                () -> assertEquals(name, reader.getName())
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
  @Transactional
  @Rollback
  void shouldFindAllReaders() {
    List<Reader> afterStartup = readerRepository.findAll();
    assertEquals(3, afterStartup.size());
    var newReader = readerRepository.save(new Reader("New Reader"));
    var secondNew = readerRepository.save(new Reader("Second new"));
    List<Reader> all = readerRepository.findAll();
    assertAll(() -> assertEquals(5, all.size()),
        () -> assertTrue(all.stream().anyMatch(newReader::equals)),
        () -> assertTrue(all.stream().anyMatch(secondNew::equals)));
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
  @Transactional
  @Rollback
  @DisplayName("Should save new reader with id 4")
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