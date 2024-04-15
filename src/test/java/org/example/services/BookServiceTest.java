package org.example.services;


import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.example.dao.BookRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookServiceTest {

  private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream err = new ByteArrayOutputStream();

  private static BookService service;
  private static BookRepository repository;

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
    repository = mock(BookRepository.class);
    service = new BookService(repository);
    err.reset();
    output.reset();
  }

  @Test
  void shouldCallRepositoryOnceWhenGetListOfBooks() {
    fail();
  }

  @Test
  void shouldCreateNewBookWithValidFieldsAndPrintMessage() {
    fail();
  }

  @Test
  void shouldPrintThatTitleNotValid(){
    fail();
  }

  @Test
  void shouldPrintThatAuthorNotValid(){
    fail();
  }

}