package org.example;

import org.example.dao.DBUtil;
import org.example.ui.ConsoleMenu;

public class App {

  public static void main(String[] args) {
    DBUtil.initDatabase();
    ConsoleMenu menu = new ConsoleMenu();
    menu.run();
  }

}
