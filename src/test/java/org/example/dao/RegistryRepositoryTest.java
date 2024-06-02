package org.example.dao;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.example.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class RegistryRepositoryTest {

  private final RegistryRepository registryRepository = new RegistryRepository();

  @BeforeAll
  static void setUpDB() {
    DBUtil.initDatabase();
  }


  @BeforeEach
  void setUp() throws SQLException {
    Util.executeSQLScript("registry.sql");
  }

  @Test
  void shouldReturnBookSuccessful() {
    var book = getFirstBook();
    registryRepository.returnBook(book);
    RegistryRepositoryException exception = assertThrows(RegistryRepositoryException.class,
        () -> registryRepository.returnBook(book));

    assertTrue(exception.getMessage().contentEquals("This book anybody doesn't borrow!"));
  }

  private Book getFirstBook() {
    var book = new Book();
    book.setId(1L);
    return book;
  }

  @Test
  void shouldThrowExceptionWhenTryToReturnNotTakenBook() {
    Book book = getFirstBook();
    registryRepository.returnBook(book);

    RegistryRepositoryException exception = assertThrows(RegistryRepositoryException.class,
        () -> registryRepository.returnBook(book));
    assertTrue(exception.getMessage().contentEquals("This book anybody doesn't borrow!"));
  }

  @Test
  void shouldThrowExceptionWhenSomeoneTryToBorrowBorrowedBook() throws RegistryRepositoryException {
    var book = getFirstBook();
    var reader = getReaderWithId(2L);
    assertThrows(RegistryRepositoryException.class,
        () -> registryRepository.borrowBook(book, reader));
  }

  private Reader getReaderWithId(Long id) {
    var reader = new Reader();
    reader.setId(id);
    return reader;
  }

  @Test
  void shouldReturnEmptyListIfReaderDoesNotBorrowBook() {
    var reader = getReaderWithId(3L);
    var emptyListOfBooks = registryRepository.getListBorrowedBooksOfReader(reader);
    assertTrue(emptyListOfBooks.isEmpty());
  }

  @Test
  void shouldReturnListOfBorrowedBooksOfReader() {
    var reader = getReaderWithId(1L);
    var books = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertEquals(2, books.size()),
        () -> assertTrue(books.stream().allMatch(book -> book.getId() == 1 || book.getId() == 2)));
  }

  @Test
  @DisplayName("First return list with two borrowed books of reader after returning book, should return list with one book")
  void shouldReturnListWithOneBorrowedBookOfReaderAfterReturningBook()
      throws RegistryRepositoryException {
    var reader = getReaderWithId(1L);
    var book = getFirstBook();
    var borrowedBooksBeforeReturn = registryRepository.getListBorrowedBooksOfReader(reader);
    registryRepository.returnBook(book);
    var borrowedBooksAfterReturn = registryRepository.getListBorrowedBooksOfReader(reader);

    assertAll(() -> assertEquals(2, borrowedBooksBeforeReturn.size()),
        () -> assertEquals(1, borrowedBooksAfterReturn.size()),
        () -> assertFalse(borrowedBooksAfterReturn.contains(book)));
  }

  @Test
  void shouldReturnReaderWhoBorrowBook() {
    var reader = getReaderWithId(1L);
    var book = getFirstBook();
    assertEquals(reader, registryRepository.getReaderOfBook(book).orElseThrow(AssertionError::new));
  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
    var thirdBook = getBookWithId(3L);
    assertTrue(registryRepository.getReaderOfBook(thirdBook).isEmpty());
  }

  private Book getBookWithId(long id) {
    var book = new Book();
    book.setId(id);
    return book;
  }

  @Test
  void shouldReturnAllReadersWithBorrowedBooks() {
    var readersWithBorrowedBooks = registryRepository.getAllReadersWithBorrowedBooks();
    var listReader = readersWithBorrowedBooks.keySet().stream().toList();
    var readersFromRepository = List.of(getReaderWithId(1L), getReaderWithId(2L),
        getReaderWithId(3L));
    assertAll(
        () -> assertTrue(listReader.containsAll(readersFromRepository)),
        () -> assertEquals(2,
            readersWithBorrowedBooks.values().stream().filter(List::isEmpty).count()),
        () -> assertTrue(
            readersWithBorrowedBooks.values().stream().anyMatch(list -> list.size() == 2))
    );
  }

  @Test
  void shouldReturnBookWithTheirCurrentReaders() {
    var reader = getReaderWithId(1L);
    var booksWithCurrentReaders = registryRepository.getAllBooksWithCurrentReaders();
    var listBook = List.of(getBookWithId(1L), getBookWithId(2L), getBookWithId(3L));

    assertAll(
        () -> assertTrue(booksWithCurrentReaders.keySet().stream().toList().containsAll(listBook)),
        () -> assertEquals(2, countBorrowedBooks(booksWithCurrentReaders, reader)),
        () -> assertEquals(1, countBooksWithoutReader(booksWithCurrentReaders))
    );
  }

  private long countBooksWithoutReader(Map<Book, Optional<Reader>> booksWithCurrentReaders) {
    return booksWithCurrentReaders.values().stream().filter(Optional::isEmpty).count();
  }

  private long countBorrowedBooks(Map<Book, Optional<Reader>> map, Reader reader) {
    return map.values().stream()
        .filter(optionalReader -> optionalReader.map(reader::equals).orElse(false)).count();
  }

}