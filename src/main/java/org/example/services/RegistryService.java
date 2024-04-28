package org.example.services;

import java.util.List;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.validator.ValidatorUtil;

public class RegistryService {

  private final RegistryRepository repository;
  private final BookService bookService;
  private final ReaderService readerService;

  public RegistryService(RegistryRepository repository, BookService bookService, ReaderService readerService)  {
      this.repository = repository;
      this.bookService = bookService;
      this.readerService = readerService;
  }

  public void borrowBook(String input) {
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
    
     repository.borrowBook(book, reader);
     System.out.println(reader.getName() + "borrow book " + book.getName());
}

  public void returnBook(Optional<Book> book) {
    if (isPrintWarningEmptyBook(book)) return;
    try {
      return readerService.findById(input).orElseThrow(() -> new RuntimeException("Reader not found"));
    } catch (Exception e){
      message.append("\n").append(e);
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

  public void returnBook(String input) {
    Book book = bookService.findById(input).orElseThrow(() -> new RuntimeException("Book not found"));
    repository.returnBook(book);
  }

  public void printBorrowedBooksByReader(String input){
    Reader reader = readerService.findById(input.strip()).orElseThrow(() -> new RuntimeException("Reader nod found!"));
    List<Book> borrowedBooks = repository.getListBorrowedBooksOfReader(reader);
    System.out.println("Borrowed books:");
    borrowedBooks.forEach(System.out::println);
  }

  public void printCurrentReaderOfBook(String input){
    Book book = bookService.findById(input).orElseThrow(() -> new RuntimeException("Book not found"));
    Optional<Reader> readerOfBook = repository.getReaderOfBook(book);
    readerOfBook.ifPresentOrElse(System.out::println, () -> System.out.println("Nobody reads this book"));
 }

}
