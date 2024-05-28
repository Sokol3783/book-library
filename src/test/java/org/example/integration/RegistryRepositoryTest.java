package org.example.integration;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;
import java.util.List;
import org.example.dao.BookRepository;
import org.example.dao.DBUtil;
import org.example.dao.ReaderRepository;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.example.util.Util;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

  private Reader getFistReader() {
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
    Reader reader = getFistReader();
    List<Book> books = registryRepository.getListBorrowedBooksOfReader(reader);
    assertAll(
        () -> assertEquals(2, books.size()),
        () -> assertTrue(books.stream().allMatch(book -> book.getId() > 0 && book.getId() < 3))
    );
  }


  @Test
  void shouldReturnListOfTwoBorrowedBooksOfReaderAfterReturningBook()
      throws RegistryRepositoryException {
    fail();
    /*
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

     */
  }


  @Test
  void shouldReturnReaderWhoBorrowBook() {
    fail();
    /*
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

     */
  }

  @Test
  void shouldReturnEmptyOptionalIfNobodyBorrowBook() {
    fail();
    assertTrue(registryRepository.getReaderOfBook(new Book("", "")).isEmpty());
  }

}