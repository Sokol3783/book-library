package org.example.services;

import java.util.regex.Pattern;
import org.example.dao.BookRepository;

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
    if (isValid(input)) {

    }
  }

  private boolean isValid (String input) {
    String[] titleAndAuthor = input.split("/");
    if (titleAndAuthor.length != 2){
      System.out.println("Invalid input! Doesn't contain author.");
      return false;
    }
    if (alertNotValidTitle(titleAndAuthor[0])) return false;
    if (alertNotValidAuthor(titleAndAuthor[1])) return false;
    return true;
  }

  private boolean alertNotValidAuthor(String author) {
    int length = author.length();
    if (length < 5 || length > 30) {
      System.err.println("Invalid length of author");
      System.err.println("Name should contain more than 5 char and less than 30 ones");
      return false;
    }

    if (!Pattern.matches("^[a-zA-Z\\s'-]+$", author)) {
      System.err.println("Author contain invalid symbols");
      return false;
    }
    return true;
  }

  private boolean alertNotValidTitle(String title) {
    return true;
  }
}
