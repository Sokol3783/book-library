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
    BookService bookService = new BookService(new BookRepository());
    ReaderService readerService = new ReaderService(new ReaderRepository());
    ConsoleMenu menu = new ConsoleMenu(bookService, readerService,
        new RegistryService(new RegistryRepository(), bookService, readerService));
    menu.run();
  }

}
