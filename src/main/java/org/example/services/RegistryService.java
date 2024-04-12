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
  }

  public void returnBook(Optional<Book> book, Optional<Reader> reader) {

  }

  public List<Book> getAllBorrowedBooksByReader(Optional<Reader> reader){
    return List.of();
  }

  public Optional<Reader> getCurrentReaderOfBook(Optional<Book> book){
    return Optional.empty();
  }

}
