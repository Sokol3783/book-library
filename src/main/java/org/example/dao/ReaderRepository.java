package org.example.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
import org.example.exception.DAOException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ReaderRepository {

  private final JdbcTemplate jdbcTemplate;

  public ReaderRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public Optional<Reader> findById(long id) {

    try {
      return jdbcTemplate.query("SELECT id, name FROM READER WHERE id =?",
          MapperUtil::mapToReader, id);
    } catch (DataAccessException e) {
      throw new DAOException(
          "Failed to retrieve a reader by ID " + id + ", due to a DB error: " + e.getMessage());
    }
  }

  public List<Reader> findAll() {
    try {
      return jdbcTemplate.query("SELECT id, name FROM READER", MapperUtil.mapToReaderList());
    } catch (SQLException e) {
      throw new DAOException("Failed to retrieve all readers due to a DB error: " + e.getMessage());
    }
  }

  public Reader save(Reader reader) {
    var key = new GeneratedKeyHolder();
    try {
      jdbcTemplate.update(connection -> getSaveReaderPrepareStatement(connection, reader), key);
      Long id = Optional.of((Long) key.getKeys().get("id")).orElseThrow(
          () -> new DAOException("Failed to return id of reader %s".formatted(reader.getName())));
      reader.setId(id);
      return reader;
    } catch (DataAccessException e) {
      throw new DAOException(
          "Failed to save reader: " + reader.getName() + ", due to a DB error " + e.getMessage());
    }
  }

  private PreparedStatement getSaveReaderPrepareStatement(Connection connection, Reader reader)
      throws SQLException {
    var statement = connection.prepareStatement("INSERT INTO reader(name) VALUES (?,?)",
        Statement.RETURN_GENERATED_KEYS);
    statement.setString(1, reader.getName());
    return statement;
  }

}
