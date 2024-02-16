package org.example.dao;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.example.entity.BookEntity;

public class BookRepository implements CRUDOperation<BookEntity> {

  private final Set<BookEntity> repository;

  private static BookRepository instance;

  private BookRepository() {
    repository = new TreeSet<>(Comparator.comparingLong(BookEntity::getId));
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
    return repository.stream().filter(s -> s.getId() == id).findFirst();
  }

  @Override
  public Optional<BookEntity> save(BookEntity object) {
    if (object.getId() == 0l) {
      object.setId(getNextId());
    }
    if (repository.add(object) | repository.contains(object))  {
      return Optional.of(object);
    }
    return Optional.empty();
  }

  private long getNextId() {
    long l = repository.stream().map(s -> s.getId()).max(Comparator.naturalOrder()).orElse(1l)
        .longValue();
    return ++l;
  }

  @Override
  public Optional<BookEntity> update(BookEntity object) {
    return save(object);
  }

  @Override
  public boolean deleteById(Long id) {
    return repository.removeIf(s -> s.getId() == id);
  }

  @Override
  public Collection<BookEntity> findAll() {
    return Set.copyOf(repository);
  }
}
