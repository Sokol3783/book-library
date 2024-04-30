package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfId;
import static org.example.validator.ValidatorUtil.validateInputOfNewBook;

import java.util.List;
import java.util.Optional;
import org.example.dao.BookRepository;
import org.example.entity.Book;

public class BookService {

  private final BookRepository repository;

  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  public List<Book> findAllBooks() {
    return repository.findAll();
  }

  public Book addNewBook(String input) {
    validateInputOfNewBook(input.strip());
    String[] titleAndAuthor= input.split("/");
    return repository.save(new Book(0l, titleAndAuthor[0], titleAndAuthor[1]));
  }

  public Optional<Book> findById(String input) {
    validateInputOfId(input.strip());
    return repository.findById(Long.parseLong(input));
  }

}
