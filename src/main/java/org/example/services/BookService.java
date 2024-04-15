package org.example.services;

import java.util.List;
import org.example.dao.BookRepository;
import org.example.entity.Book;

public class BookService {

  private final BookRepository repository;

  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  public List<Book> getAllBooks() {
    return List.of();
  }

  public void addNewBook(String input) {
  }

}
