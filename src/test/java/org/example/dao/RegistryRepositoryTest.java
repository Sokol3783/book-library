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
    Reader reader = new Reader(1L, "reader1");
    Book book = new Book(1L, "book1", "book1");
    Book book2 = new Book(2L, "book2", "book2");
    repository.borrowBook(book, new Reader());
    repository.borrowBook(book2, new Reader());
    assertAll(() -> assertThrows(RegistryRepositoryException.class, () -> repository.returnBook(book, reader)),
              () -> assertThrows(RegistryRepositoryException.class, () -> repository.returnBook(book2, reader)));
  }

  @Test
  void shouldThrowExceptionWhenReaderReturnsNotTakenBook(){
    Reader reader = new Reader(1L, "reader1");
    Book book = new Book(1L, "book1", "book1");
    Book book2 = new Book(2L, "book2", "book2");
    assertAll(() -> assertThrows(RegistryRepositoryException.class, () -> repository.returnBook(book, reader)),
              () -> assertThrows(RegistryRepositoryException.class, () -> repository.returnBook(book2, reader)));

  }

  @Test
  void shouldThrowExceptionWhenSomeoneTryToBorrowBorrowedBook() {
    Reader reader = new Reader(1L, "reader1");
    Reader reader2 = new Reader(2L, "reader2");
    Book book = new Book(1L, "book1", "book1");
    repository.borrowBook(book, reader);
    assertAll(() ->
        assertThrows(RegistryRepositoryException.class, () -> repository.borrowBook(book, reader2)), () ->
        assertTrue(repository.returnBook(book, reader)));

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