package org.example.dao;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import org.example.entity.BookEntity;

public class BookRepository implements CRUDOperation<BookEntity> {

  private final Set<BookEntity> repository;

  private static BookRepository instance;

  private BookRepository() {
    repository = new TreeSet<>();
  }

  public static BookRepository getInstance() {
    if (instance == null) {
      synchronized (ReaderRepository.class) {
        if (instance == null){
          instance = new BookRepository();
        }
      }
    }
    return instance;
  }

  @Override
  public BookEntity findById(Long id) {
    return null;
  }

  @Override
  public BookEntity save(BookEntity object) {
    return null;
  }

  @Override
  public BookEntity update(BookEntity object) {
    return null;
  }

  @Override
  public boolean deleteById(Long id) {
    return false;
  }

  @Override
  public Collection<BookEntity> findAll() {
    return null;
  }
}
