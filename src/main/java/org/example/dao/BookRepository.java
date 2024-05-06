package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.example.entity.Book;

public class BookRepository {


  public BookRepository() {
  }

  public Optional<Book> findById(long id) {
    try (Connection connection = DBUtil.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "SELECT id, name, title FROM book WHERE id = ?");
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Book book = parseBook(resultSet);
        return Optional.of(book);
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Book> findAll() {
    List<Book> books = new ArrayList<>();
    try (Connection connection = DBUtil.getConnection()) {
      PreparedStatement statement = connection.prepareStatement("SELECT id, name, title FROM book");
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        Book book = parseBook(resultSet);
        books.add(book);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return List.copyOf(books);
  }

  public Book save(Book book) {
    try (Connection connection = DBUtil.getConnection()) {
      PreparedStatement statement = connection.prepareStatement(
          "INSERT INTO book(title, author) VALUES (?,?)",
          Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, book.getName());
      statement.setString(2, book.getAuthor());
      statement.execute();
      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        book.setId(generatedKeys.getInt(1));
        return book;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    throw new RuntimeException("Book doesn't save");
  }

  private Book parseBook(ResultSet resultSet) throws SQLException {
    Book book = new Book();
    book.setId(resultSet.getLong("id"));
    book.setAuthor(resultSet.getString("author"));
    book.setName(resultSet.getString("title"));
    return book;
  }

}
