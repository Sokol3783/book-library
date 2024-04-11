package org.example.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.entity.Book;
import org.example.entity.Reader;

public class RegistryRepository {

  private final HashMap<Reader, Set<Book>> map = new HashMap<>();

  public boolean borrowBook(Book book, Reader reader) {
    Set<Book> merge = map.merge(reader, Set.of(book),
        (v1, v2) -> Stream.of(v1, v2).flatMap(Collection::stream).collect(
            Collectors.toSet()));
    return merge.contains(book);
  }

  public boolean returnBook(Book book, Reader reader) {
    return false;
  }

  public Optional<Reader> getReaderOfBook(Book book){
    return null;
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader){
    return List.of();
  }


}
