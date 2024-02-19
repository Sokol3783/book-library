package org.example;

import java.util.List;
import java.util.Scanner;
import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.entity.BookEntity;
import org.example.entity.ReaderEntity;
import org.example.factory.MenuFactory;
import org.example.service.console.BaseConsoleMenu;
import org.example.service.console.ConsoleMenu;

public class App {

  private final static Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    setUpData();
    ConsoleMenu menu = MenuFactory.getInstance(scanner).getMenu(BaseConsoleMenu.class);
    menu.run();
  }

  private static void setUpData() {
    insertBooks();
    insertReader();
  }

  private static void insertReader() {
    ReaderRepository instance = ReaderRepository.getInstance();
    List<ReaderEntity> entities = List.of(new ReaderEntity(0, "Kent Back"),
        new ReaderEntity(0, "Clark Kent"),
        new ReaderEntity(0, "Bruce Wayne"));
    entities.forEach(s -> instance.save(s));
  }

  private static void insertBooks() {
    BookRepository instance = BookRepository.getInstance();
    List<BookEntity> entities = List.of(new BookEntity(0,"Little prince", "Antoine de Saint-Exupéry"),
        new BookEntity(0, "Squealer", "George Orwell"),
        new BookEntity(0, "100 Years of Solitude", "Gabriel García Márquez"));
    entities.forEach(s -> instance.save(s));
  }

}
