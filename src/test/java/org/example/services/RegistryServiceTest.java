package org.example.services;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Stack;
import org.example.dao.RegistryRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class RegistryServiceTest {

  private static final RegistryRepository repository = Mockito.mock(RegistryRepository.class);
  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static RegistryService service;

  @BeforeAll
  static void setUpOutput() {
    System.setOut(new PrintStream(output));
  }

  @BeforeEach
  void setUp() {
    output.reset();
  }

  @AfterAll
  static void resetOutput() {
    System.setOut(System.out);
  }

  @Test
  void shouldPrintThereIsNoSuchReaderWhenOptionalReaderEmpty(){
    fail();
  }

  @Test
  void shouldPrintThereIsNoSuchBookWhenOptionalBookEmpty(){
    fail();
  }

  @Test
  void shouldPrintThatUserCantBorrowBookIfItBorrowed(){
    fail();
  }

  @Test
  void shouldPrintThatReaderBorrowBookSuccessful(){
    fail();
  }

  @Test
  void shouldPrintMessageReaderDidNotBorrowBooksIfListEmpty(){
    fail();
  }

  @Test
  void shouldPrintListOfReaderBorrowedBooks(){
    fail();
  }

  @Test
  void shouldPrintCurrentReaderOfBook(){
    fail();
  }

  @Test
  void shouldPrintThatBookNobodyReads(){
    fail();
  }

}