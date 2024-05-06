package org.example.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class DBUtil {

  private final static String URL = "jdbc:postgresql://localhost:5432/library";
  private final static String LOGIN = "sam";
  private final static String PASSWORD = "password";

  private final static DataSource dataSource = readConfig();

  private static DataSource readConfig() {
    PGSimpleDataSource source = new PGSimpleDataSource();
    source.setUser(LOGIN);
    source.setPassword(PASSWORD);
    source.setURL(URL);
    source.setConnectTimeout(1000);
    source.setDatabaseName("library");
    return source;
  }

  static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  public static void initDatabase() {
    try (Connection connection = getConnection()) {
      createTables(connection);
      insertInitialValue(connection);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void insertInitialValue(Connection connection) throws SQLException {
    String data = readSQLfromResource("data.sql");
    PreparedStatement statement = connection.prepareStatement(data);
    if (statement.execute()) {
      statement.close();
    }
  }

  private static void createTables(Connection connection) throws SQLException {
    String schema = readSQLfromResource("schema.sql");
    PreparedStatement statement = connection.prepareStatement(schema);
    if (statement.execute()) {
      statement.close();
    }
  }

  private static String readSQLfromResource(String resourceName) {
    var resourceAsStream = DBUtil.class.getClassLoader().getResourceAsStream(resourceName);
    if (resourceAsStream != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
      return reader.lines().collect(Collectors.joining("\n"));
    }
    throw new RuntimeException(resourceName + "resource not found");
  }

}
