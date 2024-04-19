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


  public void returnBook(Optional<Book> book) {
    if (isPrintWarningEmptyBook(book)) return;
    try {
      if(repository.returnBook(book.get())) {
        System.out.println("Book " + book.get().getName() + "is returned!");
      } else {
        System.out.println("Book " + book.get().getName() + "is returned!");
      }
    } catch (RegistryRepositoryException e) {
      System.err.println(e.getMessage());
    }
  }

  public void printBorrowedBooksByReader(Optional<Reader> reader){
    if(isPrintWarningEmptyReader(reader)) return;
    List<Book> borrowedBooks = repository.getListBorrowedBooksOfReader(reader.get());
    if (borrowedBooks.isEmpty()) {
      System.out.println("Reader doesn't borrow book!");
    } else {
      System.out.println("Borrowed books:");
      borrowedBooks.forEach(System.out::println);
    }
  }

  public Optional<Reader> printCurrentReaderOfBook(Optional<Book> book){
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
