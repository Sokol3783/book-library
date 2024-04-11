package org.example.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.example.entity.Book;

public class BookRepository {

  private final Set<Book> books = new TreeSet<>(Comparator.comparingLong(Book::getId));
  private long id = 0;

  public Optional<Book> findById(long id) {
    return books.stream().filter(s -> s.getId() == id).findFirst();
  }

  public List<Book> findAll(){
    return List.copyOf(books);
  }

  public Book save(Book book) {
    if (book.getId() == 0) {
      book.setId(getNextId());
    }
    books.add(book);
    return book;
  }

  private long getNextId() {
    return ++id;
  }

}
