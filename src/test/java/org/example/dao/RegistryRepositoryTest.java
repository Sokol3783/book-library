package org.example.dao;


import static org.example.util.Util.getFistReader;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistryRepositoryTest {

  private static RegistryRepository registryRepository;

  @BeforeEach
  void setUp() {
    registryRepository = new RegistryRepository();
  }

  @Test
  void shouldReturnBookSuccessful() {
    Book book = new Book("book1", "author1");
    Reader reader = new Reader("reader1");
    assertAll(() -> assertDoesNotThrow(() -> registryRepository.borrowBook(book, reader)),
        () -> assertDoesNotThrow(() -> registryRepository.returnBook(book)),
        () -> assertThrows(RegistryRepositoryException.class,
            () -> registryRepository.returnBook(book)));
  }

  @Test
  void shouldThrowExceptionWhenTryToReturnNotTakenBook() {
    Book book = new Book("book1", "book1");
    Book book2 = new Book( "book2", "book2");
    assertAll(
        () -> assertThrows(RegistryRepositoryException.class,
            () -> registryRepository.returnBook(book)),
        () -> assertThrows(RegistryRepositoryException.class,
            () -> registryRepository.returnBook(book2)));
  }

  @Test
  void shouldThrowExceptionWhenSomeoneTryToBorrowBorrowedBook() throws RegistryRepositoryException {
    Reader reader = new Reader("reader1");
    Reader reader2 = new Reader("reader2");
    Book book = new Book("book1", "book1");
    registryRepository.borrowBook(book, reader);
    assertAll(() -> assertThrows(RegistryRepositoryException.class,
            () -> registryRepository.borrowBook(book, reader2)),
        () -> registryRepository.returnBook(book));
  }

  @Test
  void shouldReturnEmptyListIfReaderDoesNotBorrowBook() {
    Reader reader = getFistReader();
    List<Book> emptyList = registryRepository.getListBorrowedBooksOfReader(reader);
    borrowTestThreeBooks(reader);
    List<Book> borrowedBooks = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertTrue(emptyList.isEmpty()),
        () -> assertFalse(borrowedBooks.isEmpty()),
        () -> assertEquals(3, borrowedBooks.size()));
  }


  @Test
  void shouldReturnListOfBorrowedBooksOfReader() {
    Reader reader = getFistReader();
    borrowTestThreeBooks(reader);
    List<Book> books = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
        () -> assertEquals(3, books.size()),
        () -> assertTrue(books.containsAll(setIdForTestBooks(getTestBooks()))));
  }


  @Test
  void shouldReturnListOfTwoBorrowedBooksOfReaderAfterReturningBook()
      throws RegistryRepositoryException {
    Reader reader = getFistReader();
    borrowTestThreeBooks(reader);
    List<Book> testBooks = new ArrayList<>(setIdForTestBooks(getTestBooks()));
    List<Book> books = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(books.isEmpty()),
        () -> assertEquals(3, books.size()));
    Book remove = testBooks.remove(0);
    registryRepository.returnBook(remove);

    List<Book> booksAfterReturnOne = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertFalse(booksAfterReturnOne.isEmpty()),
        () -> assertEquals(2, booksAfterReturnOne.size()),
        () -> assertEquals(testBooks, booksAfterReturnOne));
  }


  @Test
  void shouldReturnReaderWhoBorrowBook() {
    Reader reader = getFistReader();
    borrowTestThreeBooks(reader);
    List<Book> books = setIdForTestBooks(getTestBooks());
    Optional<Reader> firstReader = registryRepository.getReaderOfBook(books.get(0));
    Optional<Reader> secondReader = registryRepository.getReaderOfBook(books.get(1));
    Optional<Reader> thirdReader = registryRepository.getReaderOfBook(books.get(2));
    assertAll(() -> assertTrue(firstReader.isPresent()),
        () -> assertTrue(secondReader.isPresent()),
        () -> assertTrue(thirdReader.isPresent()),
        () -> assertEquals(reader, firstReader.get()),
        () -> assertEquals(reader, secondReader.get()),
        () -> assertEquals(reader, thirdReader.get()));

  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
    assertTrue(registryRepository.getReaderOfBook(new Book("", "")).isEmpty());
 }

  private void borrowTestThreeBooks(Reader reader) {
    List<Book> testBooks = setIdForTestBooks(getTestBooks());
    for (Book book : testBooks) {
      try {
        registryRepository.borrowBook(book, reader);
      } catch (RegistryRepositoryException e) {
        throw new RuntimeException(e);
      }
    }
  }

}