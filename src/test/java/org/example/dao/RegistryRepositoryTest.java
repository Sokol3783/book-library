package org.example.dao;


import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.example.util.Util.IdGenerator;
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
              () -> assertThrows(RegistryRepositoryException.class, () -> repository.returnBook(book, reader)));
  }

  @Test
  void shouldThrowExceptionWhenReaderReturnsSomeonesElseBook(){
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
    Reader reader = getReader();
    List<Book> emptyList = repository.getListBorrowedBooksOfReader(reader);
    borrowTestThreeBooks(reader);
    List<Book> borrowedBooks = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertTrue(emptyList.isEmpty()),
    () -> assertFalse(borrowedBooks.isEmpty()),
    () -> assertEquals(3, borrowedBooks.size()));
  }


  @Test
  void shouldReturnListOfBorrowedBooksOfReader(){
    Reader reader = getReader();
    borrowTestThreeBooks(reader);
    List<Book> books  = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
            () -> assertEquals(3, books.size()),
            () -> assertTrue(books.containsAll(setIdForTestBooks(getTestBooks()))));
  }


  @Test
  void shouldReturnListOfTwoBorrowedBooksOfReaderAfterReturningBook(){
    Reader reader = getReader();
    borrowTestThreeBooks(reader);
    List<Book> testBooks = new ArrayList<>(setIdForTestBooks(getTestBooks()));
    List<Book> books  = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
              () -> assertEquals(3, books.size()));
    Book remove = testBooks.remove(0);
    repository.returnBook(remove, reader);

    List<Book> booksAfterReturnOne = repository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(booksAfterReturnOne.isEmpty()),
        () -> assertEquals(2, booksAfterReturnOne.size()),
        () -> assertEquals(testBooks, booksAfterReturnOne));
  }


  @Test
  void shouldReturnReaderWhoBorrowBook(){
    Reader reader = getReader();
    borrowTestThreeBooks(reader);
    List<Book> books = setIdForTestBooks(getTestBooks());
    Optional<Reader> firstReader = repository.getReaderOfBook(books.get(0));
    Optional<Reader> secondReader = repository.getReaderOfBook(books.get(1));
    Optional<Reader> thirdReader = repository.getReaderOfBook(books.get(2));
    assertAll(() -> assertTrue(firstReader.isPresent()),
        () -> assertTrue(secondReader.isPresent()),
        () -> assertTrue(thirdReader.isPresent()),
        () -> assertEquals(reader, firstReader.get()),
        () -> assertEquals(reader, secondReader.get()),
        () -> assertEquals(reader, thirdReader.get()));

  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
      assertTrue(repository.getReaderOfBook(new Book(1L, "", "")).isEmpty());
      assertTrue(repository.getReaderOfBook(new Book(5L, "", "")).isEmpty());
      assertTrue(repository.getReaderOfBook(new Book(10L, "", "")).isEmpty());
  }

  private void borrowTestThreeBooks(Reader reader) {
    List<Book> testBooks = setIdForTestBooks(getTestBooks());
    for (Book book : testBooks){
      repository.borrowBook(book, reader);
    }
  }

  private static Reader getReader() {
    return new Reader(1L, "reader");
  }


}