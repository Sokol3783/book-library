package org.example.dao;

import java.util.Collection;
import java.util.Optional;
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
      synchronized (BookRepository.class) {
        if (instance == null){
          instance = new BookRepository();
        }
      }
    }
    return instance;
  }

  @Override
  public Optional<BookEntity> findById(Long id) {
    return Optional.empty();
  }

  @Override
  public Optional<BookEntity> save(BookEntity object) {
    return Optional.empty();
  }

  @Override
  public Optional<BookEntity> update(BookEntity object) {
    return Optional.empty();
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
