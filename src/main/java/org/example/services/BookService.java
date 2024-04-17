package org.example.services;

import static org.example.validator.ValidatorUtil.inRange;
import static org.example.validator.ValidatorUtil.invalidName;
import static org.example.validator.ValidatorUtil.invalidTitle;

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
    String[] arr = input.split("/");
    if (isValidInput(arr)) {
      Book save = repository.save(new Book(0l, arr[0], arr[1]));
      System.out.println("Book saved:\n" + save.toString());
    }
  }

  private boolean isValidInput(String[] titleAndAuthor) {
    if (titleAndAuthor.length != 2) {
      System.out.println("Invalid input! There is no / or to many / ");
      return false;
    }
    return alertNotValidTitle(titleAndAuthor[0]) && alertNotValidAuthor(titleAndAuthor[1]);
  }

  private boolean alertNotValidAuthor(String author) {
    String alert = "";
    if (inRange(author.length(), 5, 30)) {
      alert = "Invalid length of author\nName should contain more than 5 char and less than 30 ones";
    }

    if (!invalidName(author)) {
      alert += "Author must contain only letters, spaces, dashes, apostrophes!";
    }

    if (alert.isEmpty()) return true;

    System.err.println("Author is not valid\n" + alert);
    return false;
  }

  private boolean alertNotValidTitle(String title) {
    String alert = "";
    if (!inRange(title.length(), 5, 100)){
      alert = "Invalid length of title\nTitle should contain more than 5 char and less than 100 ones";
    }

    if (!invalidTitle(title)) {
      alert += "Title contains invalid symbols: |/\\#%=+*_><]";
    }

    if (alert.isEmpty()) return true;

    System.err.println("Title is not valid\n" + alert);
    return false;
  }
}
