package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.exception.DAOException;
import org.example.exception.RegistryRepositoryException;

public class RegistryRepository {

  public RegistryRepository() {
  }

  public void borrowBook(Book book, Reader reader) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            """
                INSERT INTO registry (book_id, reader_id)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM registry
                    WHERE book_id = ? 
                );
                """
        )) {
      statement.setLong(1, book.getId());
      statement.setLong(2, reader.getId());
      statement.setLong(3, book.getId());
      int i = statement.executeUpdate();
      if (i == 0) {
        throw new RegistryRepositoryException("Book is already borrowed! You can't borrow it!");
      }
    } catch (SQLException e) {
      throw new RegistryRepositoryException();
    }
  }

  public void returnBook(Book book) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM registry WHERE book_id = ?")) {
      statement.setLong(1, book.getId());
      int i = statement.executeUpdate();
      if (i == 0) {
        throw new DAOException("");
      }

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<Reader> getReaderOfBook(Book book) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            """
                SELECT reader.id, reader.name
                FROM registry
                INNER JOIN reader ON registry.reader_id = reader.id
                WHERE book_id = ?
                """
        )) {
      statement.setLong(1, book.getId());
      ResultSet resultSet = statement.executeQuery();
      return MapperUtil.mapToReader(resultSet);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            """
                SELECT book.id, book.author, book.title
                FROM registry
                LEFT JOIN book ON registry.book_id = book.id
                WHERE reader_id = ?
                """
        )) {
      statement.setLong(1, reader.getId());
      ResultSet resultSet = statement.executeQuery();
      return MapperUtil.mapToBookList(resultSet);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
