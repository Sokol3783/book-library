package org.example.util;

import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.DAOException;

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

  public static Reader getReaderWhenError() {
    return new Reader("ERROR");
  }

  public static Book getFirstBook() {
    Book book = new Book("book", "book");
    book.setId(1L);
    return book;
  }

  public static Book getBookWhenError() {
    return new Book("Error", "Error");
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

  public static Map<Reader, List<Book>> getTestReadersWithBorrowedBooks() {
    var map = new HashMap<Reader, List<Book>>();
    var reader = new Reader("reader1");
    reader.setId(1L);
    map.put(reader, List.of(new Book("book1", "book1")));
    reader = new Reader("reader2");
    reader.setId(2L);
    map.put(reader, List.of(new Book("book2", "book2"), new Book("book3", "book3")));
    reader = new Reader("reader3");
    reader.setId(3L);
    map.put(reader,List.of());
    return map;
  }

  public static Map<Book, Optional<Reader>> getTestBooksWithCurrentReader() {
    var map = new HashMap<Book, Optional<Reader>>();
    var book = new Book("book1", "book1");
    map.put(book, Optional.of(new Reader("reader1")));
    book = new Book("book2", "book2");
    book.setId(2L);
    map.put(book, Optional.empty());
    return map;
  }

  public static class IdGenerator {

    private long id = 0;

    public long getNextId() {
      return ++id;
    }
  }

  public static void executeSQLScript(Connection connection, String fileName)
      throws SQLException, DAOException {
    String data = readSQLFromTestResource(fileName);
    try (PreparedStatement statement = connection.prepareStatement(data)) {
      statement.execute();
    }
  }

  private static String readSQLFromTestResource(String resourceName) throws DAOException {
    var resourceAsStream = Util.class.getClassLoader().getResourceAsStream(resourceName);
    if (resourceAsStream != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
      return reader.lines().collect(Collectors.joining("\n"));
    }
    throw new DAOException(
        "Initiation of database failed because resource not found: " + resourceName);
  }

}