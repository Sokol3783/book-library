package org.example.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.RegistryRepositoryException;

public class RegistryRepository {

  private final Map<Reader, Set<Book>> map;

  public RegistryRepository(){
    map = new HashMap<>();
  }

  public void borrowBook(Book book, Reader reader) {
    if (isBorrowedBook(book)) throw new RegistryRepositoryException("Book is already borrowed! You can't borrow it");

    if (!map.computeIfAbsent(reader, k -> new HashSet<>()).add(book)) {
       map.computeIfPresent(reader, (k, v) -> {
        v.add(book);
        return v;
      });
    }
  }

  private boolean isBorrowedBook(Book book) {
    return map.entrySet().stream().anyMatch(s -> s.getValue().contains(book));
  }

  public void returnBook(Book book) {
    Optional<Boolean> isReturn = map.values().stream().filter(s -> s.contains(book))
        .map(s -> s.remove(book)).findAny();
    isReturn.orElseThrow(() -> new RegistryRepositoryException("This book anybody doesn't borrow!"));
  }

  public Optional<Reader> getReaderOfBook(Book book){
    return map.entrySet().stream().filter(s -> s.getValue().contains(book)).map(Entry::getKey).findFirst();
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader){
    return Optional.ofNullable(map.get(reader))
                   .map(List::copyOf)
                   .orElse(Collections.emptyList());
  }


}
