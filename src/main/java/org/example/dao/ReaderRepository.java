package org.example.dao;

import static org.example.dao.MapperUtil.mapToReader;
import static org.example.dao.MapperUtil.mapToReaderList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;
import org.example.exception.DAOException;

public class ReaderRepository {

  public ReaderRepository() {
  }

  public Optional<Reader> findById(long id) {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            "SELECT id, name FROM READER WHERE id =?")
    ) {
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();
      return mapToReader(resultSet);
    } catch (SQLException e) {
      throw new DAOException(
          "Failed to retrieve a reader by ID " + id + ", due to a DB error: " + e.getMessage());
    }
  }

  public List<Reader> findAll() {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            "SELECT id, name FROM READER")
    ) {
      var resultSet = statement.executeQuery();
      return mapToReaderList(resultSet);
    } catch (SQLException e) {
      throw new DAOException("Failed to retrieve a readers due to a DB error: " + e.getMessage());
    }
  }

  public Reader save(Reader reader) {
    try (Connection connection = DBUtil.getConnection();
        var statement = connection.prepareStatement(
            "INSERT INTO reader(name) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, reader.getName());
      statement.execute();
      var generatedId = extractGeneratedId(statement.getGeneratedKeys());
      reader.setId(generatedId);
      return reader;
    } catch (SQLException e) {
      throw new DAOException(
          "Failed to save reader: " + reader.getName() + ", due to a DB error " + e.getMessage());
    }
  }

  private int extractGeneratedId(ResultSet generatedKeys) throws SQLException {
    if (generatedKeys.next()) {
      return generatedKeys.getInt(1);
    } else {
      throw new DAOException("Failed to save new reader: no generated ID is returned from DB");
    }
  }

}
