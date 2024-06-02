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

  private final static RegistryRepository registryRepository = new RegistryRepository();
  private static final BookRepository bookRepository = new BookRepository();
  private static final ReaderRepository readerRepository = new ReaderRepository();

  @BeforeAll
  static void setUpDB() {
    DBUtil.initDatabase();
  }

  private static Book getFirstBook() {
    return bookRepository.findById(1L).orElseGet(Util::getBookWhenError);
  }

  @BeforeEach
  void setUp() throws SQLException {
    var connection = DBUtil.getConnection();
    Util.executeSQLScript(connection, "registry.sql");
  }

  @Test
  void shouldReturnBookSuccessful() {
    var book = getFirstBook();
    registryRepository.returnBook(book);
  }

  @Test
  void shouldThrowExceptionWhenTryToReturnNotTakenBook() {
    Book book = getFirstBook();
    registryRepository.returnBook(book);

    assertThrows(RegistryRepositoryException.class, () -> registryRepository.returnBook(book));
  }

  @Test
  void shouldThrowExceptionWhenSomeoneTryToBorrowBorrowedBook() throws RegistryRepositoryException {
    var book = getFirstBook();
    var reader2 = readerRepository.findById(2L).orElseGet(Util::getReaderWhenError);

    assertAll(() -> assertThrows(RegistryRepositoryException.class,
        () -> registryRepository.borrowBook(book, reader2)));
  }

  private Reader getFirstReader() {
    return readerRepository.findById(1L).orElseGet(Util::getReaderWhenError);
  }

  @Test
  void shouldReturnEmptyListIfReaderDoesNotBorrowBook() {
    Reader reader = readerRepository.findById(3L).orElseGet(Util::getReaderWhenError);
    List<Book> emptyList = registryRepository.getListBorrowedBooksOfReader(reader);
    assertTrue(emptyList.isEmpty());
  }

  @Test
  void shouldReturnListOfBorrowedBooksOfReader() {
    Reader reader = getFirstReader();
    List<Book> books = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(() -> assertEquals(2, books.size()),
        () -> assertTrue(books.stream().allMatch(book -> book.getId() > 0 && book.getId() < 3)));
  }

  @Test
  @DisplayName("First return list with two borrowed books of reader after returning book, should return list with one book")
  void shouldReturnListWithOneBorrowedBookOfReaderAfterReturningBook()
      throws RegistryRepositoryException {
    var reader = getFirstReader();
    var book = getFirstBook();
    List<Book> listBorrowedBooksBeforeReturn = registryRepository.getListBorrowedBooksOfReader(
        reader);
    registryRepository.returnBook(book);
    List<Book> listBorrowedBooksAfterReturn = registryRepository.getListBorrowedBooksOfReader(
        reader);

    assertAll(() -> assertEquals(2, listBorrowedBooksBeforeReturn.size()),
        () -> assertEquals(1, listBorrowedBooksAfterReturn.size()),
        () -> assertFalse(listBorrowedBooksAfterReturn.contains(book)));
  }

  @Test
  void shouldReturnReaderWhoBorrowBook() {
    Reader reader = getFirstReader();
    Book book = getFirstBook();
    assertEquals(reader,
        registryRepository.getReaderOfBook(book).orElseGet(() -> new Reader("NO SUCH READER")));
  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
    var thirdBook = bookRepository.findById(3L).orElseGet(Util::getBookWhenError);
    assertTrue(registryRepository.getReaderOfBook(thirdBook).isEmpty());
  }

  private static long countFirstReaderBorrowBook(Map<Book, Optional<Reader>> map, Reader reader) {
    return map.values().stream().filter(
            optionalReader -> (optionalReader.orElse(new Reader("NO SUCH READER")).equals(reader)))
        .count();
  }

  @Test
  void shouldReturnAllReadersWithBorrowedBooks() {
    var allReadersWithBorrowedBooks = registryRepository.getAllReadersWithBorrowedBooks();
    var listReader = allReadersWithBorrowedBooks.keySet().stream().toList();
    var readersFromRepository = readerRepository.findAll();
    assertAll(() -> assertTrue(listReader.containsAll(readersFromRepository)), () -> assertEquals(2,
            allReadersWithBorrowedBooks.values().stream().filter(List::isEmpty).count()),
        () -> assertTrue(
            allReadersWithBorrowedBooks.values().stream().anyMatch(list -> list.size() == 2)));
  }

  @Test
  void shouldReturnBookWithTheirCurrentReaders() {
    var reader = getFirstReader();
    var allBooksWithCurrentReaders = registryRepository.getAllBooksWithCurrentReaders();
    var listBook = bookRepository.findAll();

    assertAll(() -> assertTrue(
            allBooksWithCurrentReaders.keySet().stream().toList().containsAll(listBook)),
        () -> assertEquals(2, countFirstReaderBorrowBook(allBooksWithCurrentReaders, reader)),
        () -> assertEquals(1,
            allBooksWithCurrentReaders.values().stream().filter(Optional::isEmpty).count()));
  }

}