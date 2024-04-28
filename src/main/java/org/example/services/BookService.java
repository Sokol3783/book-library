package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfId;
import static org.example.validator.ValidatorUtil.validateInputOfNewBook;

import java.util.Optional;
import org.example.dao.BookRepository;
import org.example.entity.Book;

public class BookService {

  private final BookRepository repository;

  public BookService(BookRepository repository) {
    this.repository = repository;
  }

  public void printAllBooks() {
    System.out.println("\n Books in library:");
    repository.findAll().forEach(System.out::println);
  }

  public void addNewBook(String input) {
    validateInputOfNewBook(input.strip());
    String[] titleAndAuthor= input.split("/");
    Book save = repository.books(new Book(0l, titleAndAuthor[0], titleAndAuthor[1]));
    System.out.println(save.toString());
  }

  public Optional<Book> findById(String input) {
    input = input.strip();
    validateInputOfId(input);
    return repository.findById(Long.parseLong(input));
  }

}
