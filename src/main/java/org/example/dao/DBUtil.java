package org.example.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.postgresql.ds.PGSimpleDataSource;

public class DBUtil {

  private final static String URL = "jdbc:postgresql://localhost/library";
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

}
