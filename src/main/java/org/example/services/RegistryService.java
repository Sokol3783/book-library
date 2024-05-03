package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfTwoId;

import java.util.List;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;

public class RegistryService {

  private final RegistryRepository registryRepository;
  private final BookService bookService;
  private final ReaderService readerService;

  public RegistryService(BookService bookService, ReaderService readerService) {
    this.registryRepository = new RegistryRepository();
    this.bookService = bookService;
    this.readerService = readerService;
  }

  public RegistryService(RegistryRepository registryRepository, BookService bookService,
      ReaderService readerService) {
    this.registryRepository = registryRepository;
    this.bookService = bookService;
    this.readerService = readerService;
  }

  public Book borrowBook(String input) {

    validateInputOfTwoId(input);
    String[] bookAndReaderId = input.split("/");

    Book book = bookService.findById(bookAndReaderId[0].strip())
        .orElseThrow(() -> new RuntimeException("Book not found"));
    Reader reader = readerService.findById(bookAndReaderId[1].strip())
        .orElseThrow(() -> new RuntimeException("Reader not found"));

    registryRepository.borrowBook(book, reader);
    return book;
  }

  public Book returnBook(String input) {
    Book book = bookService.findById(input)
        .orElseThrow(() -> new RuntimeException("Book not found"));
    registryRepository.returnBook(book);
    return book;
  }

  public List<Book> findBorrowedBooksByReader(String input) {
    Reader reader = readerService.findById(input.strip())
        .orElseThrow(() -> new RuntimeException("Reader nod found!"));
    return registryRepository.getListBorrowedBooksOfReader(reader);
  }

  public Reader findCurrentReaderOfBook(String input) {
    Book book = bookService.findById(input)
        .orElseThrow(() -> new RuntimeException("Book not found"));
    Optional<Reader> readerOfBook = registryRepository.getReaderOfBook(book);
    return readerOfBook.orElseThrow(() -> new RuntimeException("Nobody reads this book"));
  }

}
