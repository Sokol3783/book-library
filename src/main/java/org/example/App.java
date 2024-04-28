package org.example;

import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.dao.RegistryRepository;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;
import org.example.ui.ConsoleMenu;

public class App {

  public static void main(String[] args) {
    ConsoleMenu menu = new ConsoleMenu(new BookService(new BookRepository())
        , new ReaderService(new ReaderRepository())
        , new RegistryService(new RegistryRepository()));
    menu.run();
  }

}
