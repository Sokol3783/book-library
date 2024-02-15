package org.example.dao;

import java.util.Collection;
import java.util.Optional;

public interface CRUDOperation<T> {

  Optional<T> findById(Long id);

  Optional<T> save(T object);

  Optional<T> update(T object);

  boolean deleteById(Long id);

  Collection<T> findAll();

}
