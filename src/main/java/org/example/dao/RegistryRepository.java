package org.example.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;

public class RegistryRepository {

  private final HashMap<Reader, Set<Book>> map = new HashMap<>();

  public boolean borrowBook(Book book, Reader reader) {
    if (isBorrowedBook(book)) throw new RegistryRepositoryException("Book is already borrowed! You can't borrow it");

    if (!map.computeIfAbsent(reader, k -> new HashSet<>()).add(book)) {
       map.computeIfPresent(reader, (k, v) -> {
        v.add(book);
        return v;
      });
    }
    return true;
  }

  private boolean isBorrowedBook(Book book) {
     return map.entrySet().stream().anyMatch(s -> s.getValue().contains(book));
  }

  public boolean returnBook(Book book, Reader reader) {
    if (!map.containsKey(reader)) throw new RegistryRepositoryException("This reader doesn't borrow book!");

    map.computeIfPresent(reader, (k,v) -> {
      if (v.remove(book)){
        return v;
      }
      throw new RegistryRepositoryException(new StringBuffer("Reader " + reader.toString()).append(" didn't borrow ")
          .append(book.toString()).toString());
    });

    return true;
  }

  public Optional<Reader> getReaderOfBook(Book book){
    return map.entrySet().stream().filter(s -> s.getValue().contains(book)).map(s -> s.getKey()).findFirst();
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader){
    Optional<Set<Book>> books = Optional.ofNullable(map.get(reader));
    if (books.isPresent()) {
      return List.copyOf(books.get());
    }
    return List.of();
  }


}
