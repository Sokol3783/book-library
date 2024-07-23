package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfId;
import static org.example.validator.ValidatorUtil.validateInputOfNewBook;

import java.util.List;
import java.util.Optional;
import org.example.dao.BookRepository;
import org.example.entity.Book;
import org.springframework.stereotype.Service;

@Service
public class BookService {

  private final BookRepository bookRepository;

  public BookService() {
    this.bookRepository = null;
  }

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<Book> findAllBooks() {
    return bookRepository.findAll();
  }

  public Book addNewBook(String input) {
    validateInputOfNewBook(input.strip());
    String[] titleAndAuthor = input.split("/");
    return bookRepository.save(new Book(titleAndAuthor[0], titleAndAuthor[1]));
  }

  public Book addNewBook(Book book) {
    return bookRepository.save(book);
  }

  public Optional<Book> findById(String input) {
    validateInputOfId(input.strip());
    return bookRepository.findById(Long.parseLong(input));
  }

  public Optional<Book> findById(Long id) {
    return bookRepository.findById(id);
  }

}
