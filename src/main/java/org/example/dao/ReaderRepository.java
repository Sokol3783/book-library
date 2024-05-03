package org.example.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import org.example.entity.Reader;

public class ReaderRepository {

  private final AtomicLong ID_GENERATOR = new AtomicLong(0);
  private final Set<Reader> readers;

  public ReaderRepository() {
    this.readers = new TreeSet<>(Comparator.comparingLong(Reader::getId));
    Reader reader = new Reader("Mike Douglas");
    reader.setId(ID_GENERATOR.incrementAndGet());
    readers.add(reader);
    reader = new Reader("Fedor Trybeckoi");
    reader.setId(ID_GENERATOR.incrementAndGet());
    readers.add(reader);
    reader = new Reader("Ivan Mazepa");
    reader.setId(ID_GENERATOR.incrementAndGet());
    readers.add(reader);
  }

  public Optional<Reader> findById(long id) {
    return readers.stream().filter(s -> s.getId() == id).findFirst();
  }

  public List<Reader> findAll() {
    return List.copyOf(readers);
  }

  public Reader save(Reader reader) {
    reader.setId(ID_GENERATOR.incrementAndGet());
    readers.add(reader);
    return reader;
  }

}
