package org.example;

import org.example.dao.DBUtil;
import org.example.exception.DAOException;
import org.example.ui.ConsoleMenu;

public class App {

  public static void main(String[] args) {
    try {
      DBUtil.initDatabase();
      ConsoleMenu menu = new ConsoleMenu();
      menu.run();
    } catch (DAOException e) {
      System.err.println("Application run is failed!\n" + e.getMessage());
    }
  }

}
