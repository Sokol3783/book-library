package org.example;

import org.example.exception.DAOException;
import org.example.ui.ConsoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

  static Logger logger = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {

    SpringApplication.run(App.class, args);

    try {
      ConsoleMenu menu = new ConsoleMenu();
      menu.run();
    } catch (DAOException e) {
      logger.error("Application run is failed!\n{}", e.getMessage());
    }
  }

}
