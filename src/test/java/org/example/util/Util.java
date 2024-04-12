package org.example.util;

import java.util.List;
import org.example.entity.Book;
import org.example.entity.Reader;

public class Util {

  public static List<Book> getTestBooks() {
    return List.of(new Book(0L,"Title 1", "Test 1" ),
            new Book(0L,"Title 2", "Test 2" ),
            new Book(0L,"Title 3", "Test 3" ));
  }

  public static List<Reader> getTestReaders() {
    return List.of(new Reader(0L,"Test 1" ),
        new Reader(0L,"Test 2" ),
        new Reader(0L,"Test 3" ));
  }

  public static class IdGenerator {
    private long id = 0;

    public long getNextId() {
      return ++id;
    }
  }

  public static Reader getReader(){
    return new Reader(1L, "reader");
  }

  public static Book getBook(){
    return new Book(1L, "book", "book");
  }

  public static List<Book> setIdForTestBooks(List<Book> testBooks) {
    IdGenerator idGenerator = new IdGenerator();
    testBooks.forEach(s -> s.setId(idGenerator.getNextId()));
    return testBooks;
  }
}
