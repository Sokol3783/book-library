package org.example.services;

import java.util.List;
import java.util.Optional;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;

public class RegistryService {

  private final RegistryRepository repository;

  public RegistryService(RegistryRepository repository)  {
      this.repository = repository;
  }

  public void borrowBook(Optional<Book> book, Optional<Reader> reader){
    if (isPrintWarningEmptyReader(reader) | isPrintWarningEmptyBook(book)) return;
    try {
      if(repository.borrowBook(book.get(), reader.get())) {
        System.out.println(reader.get().getName() + "borrow book " + book.get().getName());
      } else {
        System.err.println(reader.get().getName() + "can't borrow book!");
      }
    } catch (RegistryRepositoryException e){
        System.err.println(e.getMessage());
    }
  }


  public void returnBook(Optional<Book> book, Optional<Reader> reader) {
    if (isPrintWarningEmptyReader(reader) | isPrintWarningEmptyBook(book)) return;
    try {
      if(repository.returnBook(book.get(), reader.get())) {
        System.out.println(reader.get().getName() + "return book " + book.get().getName());
      } else {
        System.err.println(reader.get().getName() + "can't return book!");
      }
    } catch (RegistryRepositoryException e) {
      System.err.println(e.getMessage());
    }
  }

  public List<Book> getAllBorrowedBooksByReader(Optional<Reader> reader){
    if(isPrintWarningEmptyReader(reader)) {
      return List.of();
    }
    return repository.getListBorrowedBooksOfReader(reader.get());
  }

  public Optional<Reader> getCurrentReaderOfBook(Optional<Book> book){
    if(isPrintWarningEmptyBook(book)){
      return Optional.empty();
    }
    return repository.getReaderOfBook(book.get());
  }


  private boolean isPrintWarningEmptyReader(Optional<Reader> optional) {
    boolean empty = optional.isEmpty();
    if (empty) System.err.println("There is no such reader!");
    return empty;
  }

  private boolean isPrintWarningEmptyBook(Optional<Book> optional) {
    boolean empty = optional.isEmpty();
    if (empty) System.err.println("There is no such book!");
    return empty;
  }
}
