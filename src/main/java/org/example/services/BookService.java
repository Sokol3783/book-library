package org.example.services;

import java.util.List;
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
  }


}
