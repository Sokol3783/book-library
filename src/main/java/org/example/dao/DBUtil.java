package org.example.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.example.exception.DAOException;
import org.postgresql.ds.PGSimpleDataSource;

public class DBUtil {

  private final static String URL = "jdbc:postgresql://localhost:5432/book-library-sokol";
  private final static String LOGIN = "postgres";
  private final static String PASSWORD = "postgres";

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

  /**
   * Create new tables from files schema.sql (structure of database) and data.sql (some values on
   * first run of application)
   *
   * @throws DAOException If there is miss schema.sql or data sql throws an error, also if there are
   *                      problems with executing scripts
   */
  public static void initDatabase() throws DAOException {
    try (Connection connection = getConnection()) {
      List<String> scriptsInResources = List.of("schema.sql", "data.sql");
      for (String script : scriptsInResources) {
        executeSQLScript(connection, script);
      }
    } catch (SQLException e) {
      throw new DAOException(e);
    }
  }

  private static void executeSQLScript(Connection connection, String fileName)
      throws SQLException, DAOException {
    String data = readSQLFromResource(fileName);
    PreparedStatement statement = connection.prepareStatement(data);
    if (statement.execute()) {
      statement.close();
    }
  }

  private static String readSQLFromResource(String resourceName) throws DAOException {
    var resourceAsStream = DBUtil.class.getClassLoader().getResourceAsStream(resourceName);
    if (resourceAsStream != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
      return reader.lines().collect(Collectors.joining("\n"));
    }
    throw new DAOException(
        "Initiation of database failed because resource not found: " + resourceName);
  }

}
