package org.example.services;

import static org.example.util.Util.getReader;
import static org.example.util.Util.getTestReaders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.exception.ConsoleValidationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ReaderServiceTest {
  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream err = new ByteArrayOutputStream();

  private static ReaderService service;
  private static ReaderRepository repository;

  @BeforeAll
  static void beforeAll() {
    System.setOut(new PrintStream(output));
    System.setErr(new PrintStream(err));
  }

  @AfterAll
  static void afterAll() {
    System.setOut(System.out);
    System.setErr(System.err);
  }

  @BeforeEach
  void setUp() {
    repository = mock(ReaderRepository.class);
    service = new ReaderService(repository);
    err.reset();
    output.reset();
  }

  @Test
  void shouldPrintListOfReadersAndHeader() {
    List<Reader> testReaders = getTestReaders();
    when(repository.findAll()).thenReturn(testReaders);
    service.printAllReaders();
    verify(repository, times(1)).findAll();
    String message =output.toString();
    assertAll(() -> assertTrue(message.contains("Readers registered in library:")),
              () -> assertTrue(message.contains(testReaders.get(0).toString())),
              () -> assertTrue(message.contains(testReaders.get(1).toString())),
              () -> assertTrue(message.contains(testReaders.get(2).toString()))
        );
  }

  @Test
  void shouldPrintErrNameContainsInvalidSymbols(){
    ConsoleValidationException exception = assertThrows(ConsoleValidationException.class,
        () -> service.addNewReader("name_with_symbol_**!@#@@${}"));
    assertTrue(exception.getMessage().contains("Name must contain only letters, spaces, dashes, apostrophes"));
  }

  @Test
  void shouldPrintThatLengthIsInvalid(){
    ConsoleValidationException fewChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewReader("few"));
    ConsoleValidationException tooManyChar = assertThrows(ConsoleValidationException.class,
        () -> service.addNewReader("122312iagdasdghasdjkgfhsdjkfhasjkdfaskdnds"));

    assertAll(() -> assertTrue(fewChar.getMessage().contains("Name should contain more than 5 char and less than 30 ones"))
    , () -> assertTrue(tooManyChar.getMessage().contains("Name should contain more than 5 char and less than 30 ones")));
  }

  @Test
  @Disabled("The same problem which in BookServiceTests")
  void shouldPrintThatReaderCreatedAndDataReader(){
    Reader reader = getReader();
    when(repository.save(reader)).thenReturn(reader);
    service.addNewReader(getReader().getName());
    String message = output.toString();
    assertAll(() -> assertTrue(message.contains("Reader registered in library")),
        () -> assertTrue(message.contains(reader.toString())));
  }

  @Test
  void shouldReturnBookIfValidInput(){
   when(repository.findById(anyLong())).thenReturn(Optional.of(new Reader(1L, "reader")));
   assertAll(() -> assertTrue(service.findById("1").isPresent()),
        () -> assertTrue(service.findById("2").isPresent()),
        () -> assertTrue(service.findById("3").isPresent()));
  }

  @Test
  void shouldThrowValidationExceptionIfNotValidInput() {
    when(repository.findById(anyLong())).thenReturn(Optional.empty());
    assertAll( () -> assertThrows(ConsoleValidationException.class, () -> service.findById("asdasda")),
        () -> assertTrue(service.findById("1").isEmpty()));

  }
}