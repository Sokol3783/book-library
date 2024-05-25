package org.example.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

}
