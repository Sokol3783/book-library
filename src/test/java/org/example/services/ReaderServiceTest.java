package org.example.services;

import static org.example.util.Util.countRepeatedSubstrings;
import static org.example.util.Util.getReader;
import static org.example.util.Util.getTestReaders;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    service.printReaders();
    verify(repository, times(1)).findAll();
    String message = output.toString();
    assertAll(() -> assertTrue(message.contains("Readers registered in library:")),
              () -> assertTrue(message.contains(testReaders.get(0).toString())),
              () -> assertTrue(message.contains(testReaders.get(1).toString())),
              () -> assertTrue(message.contains(testReaders.get(2).toString()))
        );
  }

  @Test
  void shouldPrintErrNameContainsInvalidSymbols(){
    service.addNewReader("name_with_symbol#$%");
    service.addNewReader("name_with_symbol_**!@#@@${}");
    String errMessage = err.toString();
    assertAll(() -> assertNotEquals(0, errMessage.length()),
              () -> assertEquals(2, countRepeatedSubstrings(errMessage, "Name contains forbidden symbols")),
              () -> assertEquals(2, countRepeatedSubstrings(errMessage, "Name must contain only letters, spaces, dashes, apostrophes"))
        );
  }

  @Test
  void shouldPrintThatLengthIsInvalid(){
    service.addNewReader("few");
    service.addNewReader("122312iagdasdghasdjkgfhsdjkfhasjkdfaskdnds");
    assertAll(()  -> assertEquals(2, countRepeatedSubstrings(err.toString(), "Invalid length of name")),
              ()  -> assertEquals(2, countRepeatedSubstrings(err.toString(), "Name should contain more than 5 char and less than 30 ones")));
  }

  @Test
  void shouldPrintThatReaderCreatedAndDataReader(){
    Reader reader = getReader();
    when(repository.save(reader)).thenReturn(reader);
    service.addNewReader(getReader().getName());
    String message = output.toString();
    assertAll(() -> assertTrue(message.contains("Reader registered in library")),
        () -> assertTrue(message.contains(reader.toString())));
  }

}