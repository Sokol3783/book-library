package org.example.dao;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistryRepositoryTest {

  private static RegistryRepository repository;

  @BeforeEach
  void setUp() {
    repository = new RegistryRepository();
  }

  @Test
  void shouldReturnBookSuccessful(){
    Book book = new Book(1L,"book1", "author1");
    Reader reader = new Reader(1L, "reader1");
    assertAll(() -> assertTrue(repository.borrowBook(book, reader)),
              () -> assertTrue(repository.returnBook(book, reader)),
              () -> assertThrows(RegistryRepositoryException.class, () -> repository.borrowBook(book, reader)));
  }

  @Test
  void shouldThrowExceptionWhenReaderReturnsSomeonesBook(){
      fail();
  }

  @Test
  void shouldThrowExceptionWhenReaderReturnsNotTakenBook(){
    fail();
  }

  @Test
  void shouldReturnEmptyListIfReaderDoesntBorrowBook() {
    fail();
  }

  @Test
  void shouldReturnListOfBorrowedBooksOfReader(){
    fail();
  }

  @Test
  void shouldReturnReaderWhoBorrowBook(){
    fail();
  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
    fail();
  }
}