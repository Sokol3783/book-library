package org.example.dao;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.example.entity.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ReaderRepositoryTest {

  @Autowired
  private ReaderRepository readerRepository;

  @Test
  void shouldFindById() {
    readerRepository.findById(1L).ifPresentOrElse(
        reader -> assertAll(() -> assertEquals(1L, reader.getId()),
            () -> assertEquals("Mike Douglas", reader.getName())), Assertions::fail);
  }


  @ParameterizedTest
  @ValueSource(longs = {5L, 250L, 1000L, 12631231L})
  void shouldNotFindById(Long id) {
    assertTrue(readerRepository.findById(id).isEmpty());
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
  void shouldSaveNewReaderWithMoreThanThree() {
    var newReader = new Reader("test reader");
    var savedBook = readerRepository.save(newReader);
    var allReader = readerRepository.findAll();

    readerRepository.findById(savedBook.getId()).ifPresentOrElse(
        reader -> assertAll(
            () -> assertTrue(isNameEquals(reader, newReader)),
            () -> assertEquals(4, allReader.size())),
        Assertions::fail);
  }

  private boolean isNameEquals(Reader reader, Reader newReader) {
    return reader.getName().contentEquals(newReader.getName());
  }
}