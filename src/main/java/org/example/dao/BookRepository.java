package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.example.entity.Book;
import org.example.exception.DAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookRepository {

  private final JdbcTemplate jdbcTemplate;

  public BookRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<Book> findById(long id) {
    try {
      return jdbcTemplate.query("SELECT id, author, title FROM book WHERE id = ?",
          (ResultSetExtractor<Optional<Book>>) MapperUtil::mapToBook, id);
    } catch (DataAccessException e) {
      throw new DAOException(
          "Failed to retrieve a book by ID " + id + ", due to a DB error: " + e.getMessage());
    }
  }


  public List<Book> findAll() throws DAOException {
    try {
      return jdbcTemplate.query("SELECT id, author, title FROM book", MapperUtil.mapToBookList());
    } catch (DataAccessException e) {
      throw new DAOException("Failed to retrieve all books due to a DB error: " + e.getMessage());
    }
  }

  @Transactional
  public Book save(Book book) {
    var key = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(connection -> getSaveBookPrepareStatement(connection, book), key);
      Long id = Optional.of((Long) key.getKeys().get("id")).orElseThrow(
          () -> new DAOException("Failed to return id of book %s".formatted(book.getName())));
      book.setId(id);
      return book;
    } catch (DataAccessException e) {
      throw new DAOException(
          "Failed to save book: " + book.getName() + ", due to a DB error " + e.getMessage());

    }
  }

  private PreparedStatement getSaveBookPrepareStatement(Connection connection, Book book)
      throws SQLException {
    var statement = connection.prepareStatement("INSERT INTO book(title, author) VALUES (?,?)",
        Statement.RETURN_GENERATED_KEYS);
    statement.setString(1, book.getName());
    statement.setString(2, book.getAuthor());
    return statement;

  }

}
