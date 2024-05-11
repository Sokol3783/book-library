package org.example.dao;

import static org.example.dao.MapperUtil.mapToReader;
import static org.example.dao.MapperUtil.mapToReaderList;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        PreparedStatement statement = connection.prepareStatement(
            "SELECT id, name FROM READER WHERE id =?")
    ) {
      statement.setLong(1, id);
      ResultSet resultSet = statement.executeQuery();
      return mapToReader(resultSet);
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }

  public List<Reader> findAll() {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "SELECT id, name FROM READER")
    ) {
      ResultSet resultSet = statement.executeQuery();
      return mapToReaderList(resultSet);
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
  }


  public Reader save(Reader reader) {
    try (Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO reader(name) VALUES (?)",
            Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, reader.getName());
      statement.execute();
      ResultSet generatedKeys = statement.getGeneratedKeys();
      if (generatedKeys.next()) {
        reader.setId(generatedKeys.getInt(1));
        return reader;
      }
    } catch (SQLException e) {
      throw new DAOException(e.getMessage());
    }
    throw new DAOException("Reader doesn't save");
  }


}
