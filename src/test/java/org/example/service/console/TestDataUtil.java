package org.example.service.console;

import java.util.Collection;
import java.util.List;
import org.example.entity.BookEntity;
import org.example.entity.ReaderEntity;

public class TestDataUtil {

  static Collection<BookEntity> getTestBooks() {
    return List.of(new BookEntity(1, "Test book1", "Test author1"),
        new BookEntity(2, "Test book2", "Test author1"),
        new BookEntity(3, "Test book3", "Test author1"));
  }

  static Collection<ReaderEntity> getTestsReaders() {
    return List.of( new ReaderEntity(1, "Test1"),
        new ReaderEntity(2, "Test2"),
        new ReaderEntity(3, "Test3"));
  }

}
