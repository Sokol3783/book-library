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
import org.example.exception.DAOException;

public class BookRepository {


  public BookRepository() {
  }

  public Optional<Book> findById(long id) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT id, name, title FROM book WHERE id = ?")) {
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return mapToBook(resultSet);
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }

  public List<Book> findAll() throws DAOException {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT id, name, title FROM book");
        ResultSet resultSet = statement.executeQuery()
    ) {
      return mapToBookList(resultSet);
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }

  private List<Book> mapToBookList(ResultSet resultSet) throws SQLException {
    List<Book> books = new ArrayList<>();
    Optional<Book> book;
    while ((book = mapToBook(resultSet)).isPresent()) {
      books.add(book.get());
    }
    return books;
  }

  public Book save(Book book) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO book(title, author) VALUES (?,?)",
            Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, book.getName());
      statement.setString(2, book.getAuthor());
      statement.execute();
      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        book.setId(generatedKeys.getInt(1));
        return book;
      }
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }

    throw new DAOException("Book doesn't save");
  }

  private Optional<Book> mapToBook(ResultSet resultSet) throws SQLException {
    if (resultSet.next()) {
      Book book = new Book();
      book.setId(resultSet.getLong("id"));
      book.setAuthor(resultSet.getString("author"));
      book.setName(resultSet.getString("title"));
      return Optional.of(book);
    }
    return Optional.empty();
  }

}
