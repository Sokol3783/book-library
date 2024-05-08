package org.example.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.example.exception.DAOException;
import org.postgresql.ds.PGSimpleDataSource;

public class DBUtil {

  private final static DataSource dataSource = createDataSource();

  private static DataSource createDataSource() {
    Properties appProperties = getApplicationProperties();
    PGSimpleDataSource source = new PGSimpleDataSource();
    source.setUser(appProperties.getProperty("username"));
    source.setPassword(appProperties.getProperty("pass"));
    source.setURL(appProperties.getProperty("url"));
    source.setConnectTimeout(1000);
    source.setDatabaseName(appProperties.getProperty("db-name"));
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
      executeSQLScript(connection, "schema.sql");
      executeSQLScript(connection, "data.sql");
    } catch (SQLException e) {
      throw new DAOException(e);
    }
  }

  private static void executeSQLScript(Connection connection, String fileName)
      throws SQLException, DAOException {
    String data = readSQLFromResource(fileName);
    try (PreparedStatement statement = connection.prepareStatement(data)) {
      statement.execute();
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

  private static Properties getApplicationProperties() {
    Properties properties = new Properties();
    var stream = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
    if (stream != null) {
      try {
        properties.load(stream);
        return properties;
      } catch (IOException e) {
        throw new DAOException(e.getMessage());
      }
    }
    throw new DAOException("application.properties is missed!");
  }
}
