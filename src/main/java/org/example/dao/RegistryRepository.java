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

  private final HashMap<Reader, Set<Book>> map;

  public RegistryRepository(){
    map = new HashMap<>();
  }

  public boolean borrowBook(Book book, Reader reader) throws RegistryRepositoryException {
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

  public boolean returnBook(Book book, Reader reader) throws RegistryRepositoryException {

    if (map.containsKey(reader)){
      boolean[] isReturn = {false};
      map.computeIfPresent(reader, (k,v) ->  getBorrowedBooksBooks(book, v, isReturn));

      if (isReturn[0]) return isReturn[0];

      throw new RegistryRepositoryException("Reader " + reader.getName() + " didn't borrow "
          + book.getName());
    }
    throw new RegistryRepositoryException("This reader doesn't borrow any book!");
  }

  private Set<Book> getBorrowedBooksBooks(Book book,Set<Book> v, boolean[] isReturn)      {
      isReturn[0] = v.remove(book);
      return v;
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
