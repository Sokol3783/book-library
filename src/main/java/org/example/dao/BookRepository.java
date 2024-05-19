package org.example.dao;

import static org.example.dao.MapperUtil.mapToBook;
import static org.example.dao.MapperUtil.mapToBookList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.example.entity.Book;
import org.example.exception.DAOException;

public class BookRepository {

  public BookRepository() {
  }

  public Optional<Book> findById(long id) {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            "SELECT id, author, title FROM book WHERE id = ?")) {
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();
      return mapToBook(resultSet);
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }

  public List<Book> findAll() throws DAOException {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT id, author, title FROM book");
        ResultSet resultSet = statement.executeQuery()
    ) {
      return mapToBookList(resultSet);
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }


  public Book save(Book book) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO book(title, author) VALUES (?,?)",
            Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, book.getName());
      statement.setString(2, book.getAuthor());
      statement.execute();
      var generatedId = extractGeneratedId(statement.getGeneratedKeys());
      book.setId(generatedId);
      return book;
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }

  private int extractGeneratedId(ResultSet generatedKeys) throws SQLException {
    if(generatedKeys.next()) {
      return generatedKeys.getInt(1);
    } else {
      throw new DAOException("Failed to save new book: no generated ID is returned from DB");
    }
  }

}
