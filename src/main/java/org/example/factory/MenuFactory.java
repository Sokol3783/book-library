package org.example.factory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.service.console.BaseConsoleMenu;
import org.example.service.console.ConsoleMenu;
import org.example.service.console.MenuOption;

public class MenuFactory<T extends ConsoleMenu> {

  private final static MenuFactory factory = new MenuFactory();
  private final Map<Class, ConsoleMenu> map = new ConcurrentHashMap<>();

  private static Scanner scanner;

  public static MenuFactory getInstance(Scanner scan){
    if (scanner == null) {
      scanner = scan;
    }
    return factory;
  }

  private MenuFactory(){
  }

  public ConsoleMenu getMenu(Class<T> clazz) {
    return map.merge(clazz, getMenuImplementation(clazz), (old,recent) -> old);
  }

  private static ConsoleMenu getMenuImplementation(Class clazz) {
    if (clazz.equals(BaseConsoleMenu.class)) {
      return getBaseMenu();
    }
    throw new UnsupportedOperationException("Such console menu doesn't supported!");
  }

  private static ConsoleMenu getBaseMenu() {
    return new BaseConsoleMenu("WELCOME TO THE LIBRARY!", scanner, getBaseOptions());
  }

  private static Collection<MenuOption> getBaseOptions() {
    return List.of(new MenuOption(1,() ->BookRepository.getInstance().findAll().forEach(System.out::println), "SHOW ALL BOOKS IN THE LIBRARY"),
                    new MenuOption(2, () -> ReaderRepository.getInstance().findAll().forEach(System.out::println), "SHOW ALL READERS REGISTERED IN THE LIBRARY"));
  }


}
