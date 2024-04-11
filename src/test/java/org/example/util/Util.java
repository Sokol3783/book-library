package org.example.util;

import java.util.List;
import org.example.entity.Book;

public class Util {

  public static List<Book> getTestBooks() {
    return List.of(new Book(0l,"Title 1", "Test 1" ),
            new Book(0l,"Title 2", "Test 2" ),
            new Book(0l,"Title 3", "Test 3" ));
  }

}
