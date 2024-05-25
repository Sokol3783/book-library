package org.example.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        var statement = connection.prepareStatement(
            """
                 INSERT INTO registry (book_id, reader_id)
                 ON CONFLICT DO NOTHING
                """
        )) {
      statement.setLong(1, book.getId());
      statement.setLong(2, reader.getId());
      statement.setLong(3, book.getId());
      int borrowedBooksCount = statement.executeUpdate();
      if (borrowedBooksCount == 0) {
        throw new RegistryRepositoryException("Book is already borrowed! You can't borrow it!");
      }
    } catch (SQLException e) {
      throw new DAOException(
          "Reader with id %d failed to borrow book with id %d, due to DB error: %s"
              .formatted(book.getId(), reader.getId(), e.getMessage()));
    }
  }

  public void returnBook(Book book) {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            "DELETE FROM registry WHERE book_id = ?")) {
      statement.setLong(1, book.getId());
      int returnedBookCount = statement.executeUpdate();
      if (returnedBookCount == 0) {
        throw new RegistryRepositoryException("This book anybody doesn't borrow!");
      }
    } catch (SQLException e) {
      throw new DAOException(
          "Failed to retrieve a book by ID %d due to a DB error: %s"
              .formatted(book.getId(), e.getMessage()));
    }
  }

  public Optional<Reader> getReaderOfBook(Book book) {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            """
                SELECT reader.id, reader.name
                FROM reader 
                INNER JOIN registry ON reader.id = registry.reader_id
                WHERE book_id = ?
                """
        )) {
      statement.setLong(1, book.getId());
      var resultSet = statement.executeQuery();
      return MapperUtil.mapToReader(resultSet);
    } catch (SQLException e) {
      throw new DAOException(
          "Failed to find reader of a book by ID %d due to a DB error: %s"
              .formatted(book.getId(), e.getMessage()));
    }
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader) {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            """
                SELECT book.id, book.author, book.title
                FROM book
                INNER JOIN registry ON book.id = registry.book_id
                WHERE reader_id = ?
                """
        )) {
      statement.setLong(1, reader.getId());
      var resultSet = statement.executeQuery();
      return MapperUtil.mapToBookList(resultSet);
    } catch (SQLException e) {
      throw new DAOException(
          "Failed to find list of borrowed books of reader by ID %d, to a DB error: %s"
              .formatted(reader.getId(), e.getMessage()));
    }
  }

  public Map<Reader, List<Book>> getAllReadersWithBorrowedBooks() {
    return new HashMap<>();
  }

  public Map<Book, Optional<Reader>> getAllBooksWithCurrentReaders() {
    return new HashMap<>();
  }
}
