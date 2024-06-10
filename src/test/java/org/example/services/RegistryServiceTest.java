package org.example.services;

import static org.example.util.Util.getFirstBook;
import static org.example.util.Util.getFistReader;
import static org.example.util.Util.getTestBooks;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;
import org.example.util.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistryServiceTest {

  @Mock
  private BookService bookService;
  @Mock
  private ReaderService readerService;
  @Mock
  private RegistryRepository registryRepository;
  @InjectMocks
  private RegistryService registryService;

  @Captor
  ArgumentCaptor<Book> bookArgumentCaptor;
  @Captor
  ArgumentCaptor<Reader> readerArgumentCaptor;

  @Test
  void shouldThrowRegistryRepositoryExceptionWhenReaderIsEmpty() {
    when(readerService.findById(any())).thenThrow(new RuntimeException());
    when(bookService.findById(any())).thenReturn(Optional.of(getFirstBook()));
    assertAll(
        () -> assertThrows(RuntimeException.class, () -> registryService.borrowBook("1 / 1")),
        () -> assertThrows(RuntimeException.class,
            () -> registryService.findBorrowedBooksByReader("1")));
  }

  @Test
  void shouldThrowRegistryRepositoryExceptionWhenBookIsEmpty() {
    when(bookService.findById(any())).thenThrow(new RuntimeException("Book not found"));

    assertAll(
        () -> assertThrows(RuntimeException.class, () -> registryService.borrowBook("1 / 1")),
        () -> assertThrows(RuntimeException.class, () -> registryService.returnBook("5")),
        () -> assertThrows(RuntimeException.class,
            () -> registryService.findCurrentReaderOfBook(" 5 "))
    );
  }

  @Test
  void shouldThrowRegistryRepositoryExceptionWhenBookIsBorrowed()
      throws RegistryRepositoryException {
    when(readerService.findById(anyString())).thenReturn(Optional.of(getFistReader()));
    when(bookService.findById(anyString())).thenReturn(Optional.of(getFirstBook()));
    doThrow(new RegistryRepositoryException("Book is already borrowed! You can't borrow it")).when(
        registryRepository).borrowBook(any(), any());
    assertThrows(RegistryRepositoryException.class, () -> registryService.borrowBook("1 / 1"));
  }

  @Test
  void shouldBorrowBookSuccessfully() throws RegistryRepositoryException {
    var reader = getFistReader();
    var book = getFirstBook();

    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    when(readerService.findById(anyString())).thenReturn(Optional.of(reader));

    var borrowedBook = registryService.borrowBook("1 / 1");
    assertAll(
        () -> assertEquals(book, borrowedBook),
        () -> verify(registryRepository).borrowBook(bookArgumentCaptor.capture(),
            readerArgumentCaptor.capture()),
        () -> assertEquals(bookArgumentCaptor.getValue(), book),
        () -> assertEquals(readerArgumentCaptor.getValue(), reader),
        () -> assertEquals("book", borrowedBook.getName()),
        () -> assertEquals("book", borrowedBook.getAuthor())
    );
  }

  @Test
  void shouldReturnListOfReaderBorrowedBooks() {
    var reader = getFistReader();
    var books = getTestBooks();

    when(registryRepository.getListBorrowedBooksOfReader(reader)).thenReturn(books);
    when(readerService.findById(anyString())).thenReturn(Optional.of(reader));
    var borrowedBooks = registryService.findBorrowedBooksByReader("1");

    assertAll(
        () -> assertEquals(3, borrowedBooks.size()),
        () -> assertTrue(borrowedBooks.stream().allMatch(s -> s.getName().contains("Title"))),
        () -> assertTrue(borrowedBooks.stream().allMatch(s -> s.getAuthor().contains("Test")))
    );
  }

  @Test
  void shouldReturnCurrentReaderOfBook() {
    var book = getFirstBook();
    when(registryRepository.getReaderOfBook(any())).thenReturn(Optional.of(getFistReader()));
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    var reader = registryService.findCurrentReaderOfBook("3");
    assertEquals(reader, getFistReader());
  }

  @Test
  void shouldThrowRegistryRepositoryExceptionWhenNobodyReadsBook() {
    when(bookService.findById("1")).thenReturn(Optional.of(getFirstBook()));
    when(registryRepository.getReaderOfBook(getFirstBook())).thenReturn(Optional.empty());
    var exception = assertThrows(RuntimeException.class,
        () -> registryService.findCurrentReaderOfBook("1"));
    assertTrue(exception.getMessage().contains("Nobody reads this book"));
  }

  @Test
  void shouldReturnBook() throws RegistryRepositoryException {
    var book = getFirstBook();
    when(bookService.findById(any())).thenReturn(Optional.of(book));
    var returnedBook = registryService.returnBook("1");

    assertAll(
        () -> assertEquals(book, returnedBook),
        () -> assertEquals("book", book.getAuthor()),
        () -> assertEquals("book", book.getName()),
        () -> verify(registryRepository, times(1)).returnBook(any())
    );
  }

  @Test
  void shouldThrowRegistryRepositoryExceptionWhenNobodyBorrowBook()
      throws RegistryRepositoryException {
    var message = "Anybody doesn't borrow this book!";
    var book = getFirstBook();
    doThrow(new RegistryRepositoryException(message)).when(registryRepository).returnBook(book);
    when(bookService.findById(anyString())).thenReturn(Optional.of(book));
    RegistryRepositoryException exception = assertThrows(
        RegistryRepositoryException.class, () -> registryService.returnBook("1"));
    assertTrue(exception.getMessage().contains(message));
  }

  @Test
  void shouldReturnMapKeyReaderValueListOfBook() {
    when(registryRepository.getAllReadersWithBorrowedBooks()).thenReturn(
        Util.getTestReadersWithBorrowedBooks());
    var allReadersWithBorrowedBooks = registryService.getAllReadersWithBorrowedBooks();
    assertAll(
        () -> assertFalse(allReadersWithBorrowedBooks.containsValue(null)),
        () -> assertEquals(3, allReadersWithBorrowedBooks.size()),
        () -> assertEquals(1,
            getListBorrowedBooksByReaderName("reader1", allReadersWithBorrowedBooks).size()),
        () -> assertEquals(2,
            getListBorrowedBooksByReaderName("reader2", allReadersWithBorrowedBooks).size()),
        () -> assertTrue(
            getListBorrowedBooksByReaderName("reader3", allReadersWithBorrowedBooks).isEmpty())
    );
  }

  private List<Book> getListBorrowedBooksByReaderName(String readerName,
      Map<Reader, List<Book>> allReadersWithBorrowedBooks) {
    return allReadersWithBorrowedBooks.entrySet().stream()
        .filter(entry -> entry.getKey().getName().contentEquals(readerName)).map(Entry::getValue)
        .findAny().orElse(null);
  }

  @Test
  void shouldReturnMapKeyBookValueOptionalReader() {
    when(registryRepository.getAllBooksWithCurrentReaders()).thenReturn(
        Util.getTestBooksWithCurrentReader());
    var allBooksWithBorrowers = registryService.getAllBooksWithBorrowers();
    assertAll(
        () -> assertEquals(2, allBooksWithBorrowers.size()),
        () -> assertFalse(allBooksWithBorrowers.containsValue(null)),
        () -> assertTrue(getReaderByBookTitle("book1", allBooksWithBorrowers).isPresent()),
        () -> assertTrue(getReaderByBookTitle("book2", allBooksWithBorrowers).isEmpty())
    );

  }

  private Optional<Reader> getReaderByBookTitle(String bookTitle,
      Map<Book, Optional<Reader>> allBooksWithBorrowers) {
    return allBooksWithBorrowers.entrySet().stream()
        .filter(entry -> entry.getKey().getName().contentEquals(bookTitle))
        .map(Entry::getValue).findAny().orElse(null);
  }

}