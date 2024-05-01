package org.example.services;

import java.util.List;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.validator.ValidatorUtil;

public class RegistryService {

  private final RegistryRepository registryRepository;
  private final BookService bookService;
  private final ReaderService readerService;

  public RegistryService(RegistryRepository registryRepository, BookService bookService, ReaderService readerService)  {
      this.registryRepository = registryRepository;
      this.bookService = bookService;
      this.readerService = readerService;
  }

  public Book borrowBook(String input) {
    StringBuilder message = new StringBuilder();
    Book book; Reader reader;

    if (ValidatorUtil.inputContainsSingleSlash(input)) {
      String[] bookAndReaderId = input.split("/");
      book = findBookOrGetErrorMessages(bookAndReaderId[0].strip(), message);
      reader = findReaderOrGetExceptionMessage(bookAndReaderId[1].strip(), message);
    } else {
      message.append("\nInput should contain single dash!");
      book = findBookOrGetErrorMessages(input.strip(), message);
      reader = findReaderOrGetExceptionMessage("", message);
    }
    
    if (!message.isEmpty()) {
      throw new RuntimeException(message.toString());
    }
    
    registryRepository.borrowBook(book, reader);
    return book;
}

  private Reader findReaderOrGetExceptionMessage(String input, StringBuilder message) {
    try {
      return readerService.findById(input).orElseThrow(() -> new RuntimeException("Reader not found"));
    } catch (Exception e){
      message.append("\n").append(e.getMessage());
      return null;
    }
  }

  private Book findBookOrGetErrorMessages(String input, StringBuilder message){
    try {
      return bookService.findById(input).orElseThrow(() -> new RuntimeException("Book not found"));
    } catch (Exception e){
      message.append("\n").append(e.getMessage());
      return null;
    }
  }

  public Book returnBook(String input) {
    Book book = bookService.findById(input).orElseThrow(() -> new RuntimeException("Book not found"));
    registryRepository.returnBook(book);
    return book;
  }

  public List<Book> findBorrowedBooksByReader(String input){
    Reader reader = readerService.findById(input.strip()).orElseThrow(() -> new RuntimeException("Reader nod found!"));
    return registryRepository.getListBorrowedBooksOfReader(reader);
  }

  public Reader findCurrentReaderOfBook(String input){
    Book book = bookService.findById(input).orElseThrow(() -> new RuntimeException("Book not found"));
    Optional<Reader> readerOfBook = registryRepository.getReaderOfBook(book);
    return readerOfBook.orElseThrow(() -> new RuntimeException("Nobody reads this book"));
 }

}
