package org.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.entity.Book;
import org.example.entity.Reader;

public class MapperUtil {

  static Optional<Book> mapToBook(ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      var book = new Book();
      book.setId(resultSet.getLong("id"));
      book.setAuthor(resultSet.getString("author"));
      book.setName(resultSet.getString("title"));
      return Optional.of(book);
    }
    return Optional.empty();
  }

  static List<Book> mapToBookList(ResultSet resultSet) throws SQLException {
    List<Book> books = new ArrayList<>();
    Optional<Book> book;
    while ((book = mapToBook(resultSet)).isPresent()) {
      books.add(book.get());
    }
    return books;
  }

  static Optional<Reader> mapToReader(ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      var reader = new Reader();
      reader.setName(resultSet.getString("name"));
      reader.setId(resultSet.getLong("id"));
      return Optional.of(reader);
    }
    return Optional.empty();
  }

  static List<Reader> mapToReaderList(ResultSet resultSet) throws SQLException {
    List<Reader> readers = new ArrayList<>();
    Optional<Reader> reader;
    while ((reader = mapToReader(resultSet)).isPresent()) {
      readers.add(reader.get());
    }
    return readers;
  }

  static Map<Reader, List<Book>> mapToReadersAndBorrowedBooks(ResultSet resultSet)
      throws SQLException {
    var readersBooks = new LinkedHashMap<Reader, List<Book>>();
    while (resultSet.next()) {
      var readerOptional = mapToReaderByReaderId(resultSet);
      var borrowedBookOptional = mapToBookByBookId(resultSet);
      readerOptional.ifPresent(
          reader -> borrowedBookOptional.ifPresentOrElse(
              book -> readersBooks
                  .computeIfAbsent(reader, borrowedBooks -> new ArrayList<>())
                  .add(book),
              () -> readersBooks.putIfAbsent(reader, new ArrayList<>())
          )
      );
    }
    return readersBooks;
  }

  public static Map<Book, Optional<Reader>> mapToBooksBorrowedByReader(ResultSet resultSet)
      throws SQLException {
    var booksCurrentReader = new LinkedHashMap<Book, Optional<Reader>>();
    while (resultSet.next()) {
      Optional<Book> optionalBook = mapToBookByBookId(resultSet);
      Optional<Reader> reader = mapToReaderByReaderId(resultSet);
      optionalBook.ifPresent(book -> booksCurrentReader.put(book, reader));
    }
    return booksCurrentReader;
  }

  private static Optional<Book> mapToBookByBookId(ResultSet resultSet) throws SQLException {
    long bookId = resultSet.getLong("book_id");
    if (bookId != 0) {
      var book = new Book();
      book.setId(bookId);
      book.setName(resultSet.getString("title"));
      book.setAuthor(resultSet.getString("author"));
      return Optional.of(book);
    }
    return Optional.empty();
  }

  private static Optional<Reader> mapToReaderByReaderId(ResultSet resultSet) throws SQLException {
    long readerId = resultSet.getLong("reader_id");
    if (readerId != 0) {
      var reader = new Reader();
      reader.setId(readerId);
      reader.setName(resultSet.getString("name"));
      return Optional.of(reader);
    }
    return Optional.empty();
  }

}
