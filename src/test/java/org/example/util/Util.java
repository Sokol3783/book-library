package org.example.util;

import static java.lang.Thread.sleep;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.example.entity.Book;
import org.example.entity.Reader;

public class Util {

  public static List<Book> getTestBooks() {
    return List.of(new Book("Title 1", "Test 1"),
        new Book("Title 2", "Test 2"),
        new Book("Title 3", "Test 3"));
  }

  public static List<Reader> getTestReaders() {
    return List.of(new Reader("Test 1"),
        new Reader("Test 2"),
        new Reader("Test 3"));
  }

  public static Reader getFistReader() {
    Reader reader = new Reader("reader");
    reader.setId(1L);
    return reader;
  }

  public static Book getFirstBook() {
    Book book = new Book("book", "book");
    book.setId(1L);
    return book;
  }

  public static List<Book> setIdForTestBooks(List<Book> testBooks) {
    IdGenerator idGenerator = new IdGenerator();
    testBooks.forEach(s -> s.setId(idGenerator.getNextId()));
    return testBooks;
  }

  public static int countRepeatedSubstrings(String str, String target) {
    return (str.length() - str.replace(target, "").length()) / target.length();
  }

  public static void inputWithSleep(String... data) throws InterruptedException {
    String join = String.join("\n", data);
    System.setIn(new ByteArrayInputStream(join.getBytes()));
    sleep(200);
  }

  public static class IdGenerator {

    private long id = 0;

    public long getNextId() {
      return ++id;
    }
  }
}