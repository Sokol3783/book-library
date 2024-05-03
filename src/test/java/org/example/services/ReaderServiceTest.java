package org.example.services;

import static org.example.util.Util.getFistReader;
import static org.example.util.Util.getTestReaders;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.exception.ConsoleValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ReaderServiceTest {

  private static ReaderService readerService;
  private static ReaderRepository readerRepository;


  @BeforeEach
  void setUp() {
    readerRepository = mock(ReaderRepository.class);
    readerService = new ReaderService(readerRepository);
  }

  @Test
  void shouldThrowListOfReadersAndHeader() {
    List<Reader> testReaders = getTestReaders();
    when(readerRepository.findAll()).thenReturn(testReaders);
    List<Reader> allReaders = readerService.findAllReaders();
    verify(readerRepository, times(1)).findAll();
    assertAll(
        () -> assertEquals(3, allReaders.size()),
        () -> assertTrue(testReaders.stream().allMatch(s -> s.getName().contains("Test")))
    );
  }

  @Test
  void shouldThrowErrNameContainsInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(ConsoleValidationException.class,
        () -> readerService.addNewReader("name_with_symbol_**!@#@@${}"));
    assertTrue(exception.getMessage()
        .contains("Name must contain only letters, spaces, dashes, apostrophes"));
  }

  @Test
  void shouldThrowThatLengthIsInvalid() {
    ConsoleValidationException fewChar = assertThrows(ConsoleValidationException.class,
        () -> readerService.addNewReader("few"));
    ConsoleValidationException tooManyChar = assertThrows(ConsoleValidationException.class,
        () -> readerService.addNewReader("122312iagdasdghasdjkgfhsdjkfhasjkdfaskdnds"));

    assertAll(() -> assertTrue(
            fewChar.getMessage().contains("Name should contain more than 5 char and less than 30 ones"))
        , () -> assertTrue(tooManyChar.getMessage()
            .contains("Name should contain more than 5 char and less than 30 ones")));
  }

  @Test
  @Disabled("The same problem which in BookServiceTests")
  void shouldThrowThatReaderCreatedAndDataReader() {
    Reader reader = getFistReader();
    when(readerRepository.save(reader)).thenReturn(reader);
    Reader saved = readerService.addNewReader(getFistReader().getName());
    assertAll(() -> assertEquals(reader, saved),
        () -> assertEquals("reader", saved.getName())
    );
  }

  @Test
  void shouldReturnBookIfValidInput() {
    when(readerRepository.findById(anyLong())).thenReturn(Optional.of(new Reader("reader")));
    assertAll(() -> assertTrue(readerService.findById("1").isPresent()),
        () -> assertTrue(readerService.findById("2").isPresent()),
        () -> assertTrue(readerService.findById("3").isPresent()));
  }

  @Test
  void shouldThrowValidationExceptionIfNotValidInput() {
    when(readerRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertAll(
        () -> assertThrows(ConsoleValidationException.class,
            () -> readerService.findById("asdasda")),
        () -> assertTrue(readerService.findById("1").isEmpty()));

  }
}