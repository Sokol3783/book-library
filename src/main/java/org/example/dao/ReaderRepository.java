package org.example.dao;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.example.entity.ReaderEntity;

public class ReaderRepository implements CRUDOperation<ReaderEntity> {

  private final Set<ReaderEntity> repository;

  private static ReaderRepository instance;

  private ReaderRepository() {
    repository = new TreeSet<>();
  }

  public static ReaderRepository getInstance() {
    if (instance == null) {
      synchronized (ReaderRepository.class) {
        if (instance == null) {
          instance = new ReaderRepository();
        }
      }
    }
    return instance;
  }

  @Override
  public Optional<ReaderEntity> findById(Long id) {
    return repository.stream().filter(s -> s.getId() == id).findFirst();
  }

  @Override
  public Optional<ReaderEntity> save(ReaderEntity object) {
    return Optional.empty();
  }

  @Override
  public Optional<ReaderEntity> update(ReaderEntity object) {
    return save(object);
  }

  @Override
  public boolean deleteById(Long id) {
    return repository.removeIf(s -> s.getId() == id);
  }

  @Override
  public Collection<ReaderEntity> findAll() {
    return Set.copyOf(repository);
  }
}
