package org.example.dao;


import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.example.util.Util;
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
    Reader reader = new Reader(1L, "reader");
    List<Book> emptyList = repository.getListBorrowedBooksOfReader(reader);
    borrowThreeBooks(reader);
    List<Book> borrowedBooks = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertTrue(emptyList.isEmpty()),
    () -> assertFalse(borrowedBooks.isEmpty()),
    () -> assertEquals(3, borrowedBooks.size()));
  }


  @Test
  void shouldReturnListOfBorrowedBooksOfReader(){
    Reader reader = new Reader(1L, "reader");
    borrowThreeBooks(reader);
    List<Book> books  = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
            () -> assertEquals(3, books.size()),
            () -> assertTrue(books.containsAll(getTestBooks())));
  }

  @Test
  void shouldReturnListOfTwoBorrowedBooksOfReaderAfterReturningBook(){
    Reader reader = new Reader(1L, "reader");
    borrowThreeBooks(reader);
    List<Book> testBooks = getTestBooks();
    List<Book> books  = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
              () -> assertEquals(3, books.size()));

    repository.returnBook(getTestBooks().get(0), reader);

    Book remove = testBooks.remove(0);
    List<Book> booksAfterReturnOne = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
        () -> assertEquals(2, books.size()),
        () -> assertTrue(booksAfterReturnOne.stream().noneMatch(s -> s.equals(remove))),
        () -> assertTrue(booksAfterReturnOne.containsAll(testBooks)));

  }

  @Test
  void shouldReturnReaderWhoBorrowBook(){
    fail();
  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
    fail();
  }

  private void borrowThreeBooks(Reader reader) {
    for (Book book : getTestBooks()){
      repository.borrowBook(book, reader);
    }
  }
}