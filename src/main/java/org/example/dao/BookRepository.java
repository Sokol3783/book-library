package org.example.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import org.example.entity.Book;

public class BookRepository {

  private final AtomicLong ID_GENERATOR = new AtomicLong(0);

  private final Set<Book> books;

  public BookRepository() {
    books = new TreeSet<>(Comparator.comparingLong(Book::getId));
    books.add(new Book(ID_GENERATOR.incrementAndGet(), "The Dark Tower", "Steven King"));
    books.add(new Book(ID_GENERATOR.incrementAndGet(), "The name of the Wind", "Patric Rotfuss"));
    books.add(new Book(ID_GENERATOR.incrementAndGet(), "A Game of Thrones", "George Martin"));
  }

  public Optional<Book> findById(long id) {
    return books.stream().filter(s -> s.getId() == id).findFirst();
  }

  public List<Book> findAll() {
    return List.copyOf(books);
  }

  public Book save(Book book) {
    book.setId(ID_GENERATOR.incrementAndGet());
    books.add(book);
    return book;
  }
}
