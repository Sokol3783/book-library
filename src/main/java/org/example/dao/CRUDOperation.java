package org.example.dao;

import java.util.Collection;

public interface CRUDOperation<T> {

  T findById(Long id);

  T save(T object);

  T update(T object);
  boolean deleteById(Long id);

  Collection<T> findAll();

}
