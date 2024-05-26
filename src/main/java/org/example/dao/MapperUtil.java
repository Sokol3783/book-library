package org.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    Map<Reader, List<Book>> readersBooks = new HashMap<>();
    while (resultSet.next()) {
      var reader = mapToReaderByReader_ID(resultSet);
      var borrowedBook = mapToBookByBook_ID(resultSet);
      reader.ifPresent(
          value -> readersBooks.merge(value, bookInList(borrowedBook), MapperUtil::mergeListBooks));
    }
    return readersBooks;
  }

  private static List<Book> bookInList(Optional<Book> borrowedBook) {
    var books = new ArrayList<Book>();
    borrowedBook.ifPresent(books::add);
    return books;
  }

  private static List<Book> mergeListBooks(List<Book> books, List<Book> books2) {
    books.addAll(books2);
    return books;
  }

  public static Map<Book, Optional<Reader>> mapToBooksBorrowedByReader(ResultSet resultSet)
      throws SQLException {
    var booksCurrentReader = new HashMap<Book, Optional<Reader>>();
    while (resultSet.next()) {
      Optional<Book> book = mapToBookByBook_ID(resultSet);
      Optional<Reader> reader = mapToReaderByReader_ID(resultSet);
      book.ifPresent(k -> booksCurrentReader.put(k, reader));
    }
    return booksCurrentReader;
  }

  private static Optional<Book> mapToBookByBook_ID(ResultSet resultSet) throws SQLException {
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

  private static Optional<Reader> mapToReaderByReader_ID(ResultSet resultSet) throws SQLException {
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
