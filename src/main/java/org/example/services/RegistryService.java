package org.example.services;

import java.util.List;
import java.util.Optional;
import org.example.entity.Book;
import org.example.entity.Reader;

public class RegistryService {

  public void borrowBook(String bookId, String readerId){
  }

  public void returnBook(String bookId, String readerId) {

  }

  public List<Book> getAllBorrowedBooksByReader(String readerId){
    return List.of();
  }

  public Optional<Reader> getCurrentReaderOfBook(){
    return Optional.empty();
  }

}
