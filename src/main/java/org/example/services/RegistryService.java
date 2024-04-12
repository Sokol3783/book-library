package org.example.services;

import java.util.List;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;

public class RegistryService {

  private final RegistryRepository repository;

  public RegistryService(RegistryRepository repository)  {
      this.repository = repository;
  }

  public void borrowBook(Optional<Book> book, Optional<Reader> reader){
    if (isWarningEmptyReader(reader) | isWarningEmptyBook(book)) return;
    try {
      if(repository.borrowBook(book.get(), reader.get())) {
        System.out.println();
      }
    } catch (RuntimeException e){
        System.out.println(e.getMessage());
    }
  }


  public void returnBook(Optional<Book> book, Optional<Reader> reader) {
    if (isWarningEmptyReader(reader) | isWarningEmptyBook(book)) return;
    repository.returnBook(book.get(), reader.get());
    try {
      if(repository.borrowBook(book.get(), reader.get())) {
        System.out.println();
      }
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }

  public List<Book> getAllBorrowedBooksByReader(Optional<Reader> reader){
    if(isWarningEmptyReader(reader)) {
      return List.of();
    }
    return repository.getListBorrowedBooksOfReader(reader.get());
  }

  public Optional<Reader> getCurrentReaderOfBook(Optional<Book> book){
    if(isWarningEmptyBook(book)){
      return Optional.empty();
    }
    return repository.getReaderOfBook(book.get());
  }


  private boolean isWarningEmptyReader(Optional<Reader> optional) {
    boolean empty = optional.isEmpty();
    if (empty) System.out.println("There is no such reader!");
    return empty;
  }

  private boolean isWarningEmptyBook(Optional<Book> optional) {
    boolean empty = optional.isEmpty();
    if (empty) System.out.println("There is no such book!");
    return empty;
  }
}
