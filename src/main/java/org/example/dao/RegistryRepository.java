package org.example.dao;

import java.util.List;
import java.util.Optional;
import org.example.entity.Book;
import org.example.entity.Reader;

public class RegistryRepository {

  public RegistryRepository() {
  }

  public void borrowBook(Book book, Reader reader) {
   /*
    if (isBorrowedBook(book)) {
      throw new RegistryRepositoryException("Book is already borrowed! You can't borrow it");
    }
    map.computeIfAbsent(reader, k -> new HashSet<>()).add(book);
    */
  }

  private boolean isBorrowedBook(Book book) {
    return false;
    /*
    return map.entrySet().stream().anyMatch(s -> s.getValue().contains(book));
     */
  }

  public void returnBook(Book book) {
/*
    map.values().stream()
        .filter(s -> s.remove(book))
        .findAny().
        orElseThrow(() -> new RegistryRepositoryException("This book anybody doesn't borrow!"));
 */
  }

  public Optional<Reader> getReaderOfBook(Book book) {
    return Optional.empty();
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader) {
    return List.of();
  }

}
