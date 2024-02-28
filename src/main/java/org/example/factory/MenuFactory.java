package org.example.factory;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.entity.BookEntity;
import org.example.entity.ReaderEntity;
import org.example.service.console.BaseConsoleMenu;
import org.example.service.console.ConsoleMenu;
import org.example.service.console.MenuOption;

public class MenuFactory<T extends ConsoleMenu> {

  private final static MenuFactory factory = new MenuFactory();
  private static Scanner scanner;
  private final Map<Class, ConsoleMenu> map = new ConcurrentHashMap<>();

  private MenuFactory() {
  }

  public static MenuFactory getInstance(Scanner scan) {
    if (scanner == null) {
      scanner = scan;
    }
    return factory;
  }

  private static ConsoleMenu getMenuImplementation(Class clazz) {
    if (clazz.equals(BaseConsoleMenu.class)) {
      return getBaseMenu();
    }
    throw new UnsupportedOperationException("Such console menu doesn't supported!");
  }

  private static ConsoleMenu getBaseMenu() {
    return new BaseConsoleMenu("WELCOME TO THE LIBRARY!", scanner, convertToLinkedMap(getBaseOptions()));
  }

  private static List<MenuOption> getBaseOptions() {
    return List.of(
        new MenuOption(1, () -> printBooks(BookRepository.getInstance().findAll()),
            "SHOW ALL BOOKS IN THE LIBRARY"),
        new MenuOption(2, () -> printReaders(ReaderRepository.getInstance().findAll()),
            "SHOW ALL READERS REGISTERED IN THE LIBRARY"));
  }

  private static LinkedHashMap<Integer, MenuOption> convertToLinkedMap(Collection<MenuOption> menuOptions) {
    return menuOptions.stream().sorted(Comparator.comparing(MenuOption::getNumber))
        .collect(Collectors.toMap(MenuOption::getNumber, x -> x, (x, y) -> y, LinkedHashMap::new));
  }

  private static void printReaders(Collection<ReaderEntity> all) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(String.format(" %2s %27s\n", "id", "name"));
    buffer.append(all.stream().sorted(Comparator.comparing(ReaderEntity::getId)).map(
            s -> String.format(" %4d %-25s", s.getId(), s.getName()))
        .collect(Collectors.joining("\n")));
    System.out.println(buffer);
  }

  private static void printBooks(Collection<BookEntity> all) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(String.format(" %2s %13s %25s\n", "id", "title", "author"));
    buffer.append(all.stream().sorted(Comparator.comparing(BookEntity::getId)).map(
            s -> String.format(" %4d %-25s %-25s", s.getId(), s.getName(), s.getAuthor()))
            .collect(Collectors.joining("\n")));
    System.out.println(buffer);
  }

  public ConsoleMenu getMenu(Class<T> clazz) {
    return map.merge(clazz, getMenuImplementation(clazz), (old, recent) -> old);
  }

}
