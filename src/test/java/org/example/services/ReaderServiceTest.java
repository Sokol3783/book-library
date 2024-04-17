package org.example.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.example.dao.ReaderRepository;
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
  void shouldPrintListOfReaders() {
      fail();
  }

  @Test
  void shouldPrintInvalidName(){
    fail();
  }

  @Test
  void shouldPrintThatReaderCreatedAndDataReader(){
    fail();
  }

}