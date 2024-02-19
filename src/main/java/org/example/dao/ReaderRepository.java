package org.example.dao;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.example.entity.ReaderEntity;

public class ReaderRepository implements CRUDOperation<ReaderEntity> {

  private final Set<ReaderEntity> repository;

  private static ReaderRepository instance;

  private ReaderRepository() {
    repository = new TreeSet<>(Comparator.comparingLong(ReaderEntity::getId));
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
    if (object.getId() == 0l) {
      object.setId(getNextId());
    }
    if (repository.add(object) | repository.contains(object))  {
      return Optional.of(object);
    }
    return Optional.empty();
  }

  private long getNextId() {
    long l = repository.stream().map(s -> s.getId()).max(Comparator.naturalOrder()).orElse(0l)
        .longValue();
    return ++l;
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
