package org.example.services;

import static org.example.util.Util.getReader;
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

  private static ReaderService service;
  private static ReaderRepository repository;


  @BeforeEach
  void setUp() {
    repository = mock(ReaderRepository.class);
    service = new ReaderService(repository);
  }

  @Test
  void shouldThrowListOfReadersAndHeader() {
    List<Reader> testReaders = getTestReaders();
    when(repository.findAll()).thenReturn(testReaders);
    List<Reader> allReaders = service.findAllReaders();
    verify(repository, times(1)).findAll();
    assertAll(
        () -> assertEquals(3, allReaders.size()),
        () -> assertTrue(testReaders.stream().allMatch(s -> s.getName().contains("Test")))
    );
  }

  @Test
  void shouldThrowErrNameContainsInvalidSymbols() {
    ConsoleValidationException exception = assertThrows(ConsoleValidationException.class,
        () -> service.addNewReader("name_with_symbol_**!@#@@${}"));
    assertTrue(exception.getMessage()
        .contains("Name must contain only letters, spaces, dashes, apostrophes"));
  }

  @Test
  void shouldThrowThatLengthIsInvalid() {
    ConsoleValidationException fewChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewReader("few"));
    ConsoleValidationException tooManyChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewReader("122312iagdasdghasdjkgfhsdjkfhasjkdfaskdnds"));

    assertAll(() -> assertTrue(
            fewChar.getMessage().contains("Name should contain more than 5 char and less than 30 ones"))
        , () -> assertTrue(tooManyChar.getMessage()
            .contains("Name should contain more than 5 char and less than 30 ones")));
  }

  @Test
  @Disabled("The same problem which in BookServiceTests")
  void shouldThrowThatReaderCreatedAndDataReader() {
    Reader reader = getReader();
    when(repository.save(reader)).thenReturn(reader);
    Reader saved = service.addNewReader(getReader().getName());
    assertAll(() -> assertEquals(reader, saved),
        () -> assertEquals("reader", saved.getName())
    );
  }

  @Test
  void shouldReturnBookIfValidInput() {
    when(repository.findById(anyLong())).thenReturn(Optional.of(new Reader(1L, "reader")));
    assertAll(() -> assertTrue(service.findById("1").isPresent()),
        () -> assertTrue(service.findById("2").isPresent()),
        () -> assertTrue(service.findById("3").isPresent()));
  }

  @Test
  void shouldThrowValidationExceptionIfNotValidInput() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());
    assertAll(
        () -> assertThrows(ConsoleValidationException.class, () -> service.findById("asdasda")),
        () -> assertTrue(service.findById("1").isEmpty()));

  }
}