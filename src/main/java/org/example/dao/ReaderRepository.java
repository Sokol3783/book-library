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
  private final Set<Reader> readers= new TreeSet<>(Comparator.comparingLong(Reader::getId));

  public ReaderRepository() {
    readers.add(new Reader(ID_GENERATOR.incrementAndGet(), "Mike Douglas"));
    readers.add(new Reader(ID_GENERATOR.incrementAndGet(), "Fedor Trybeckoi"));
    readers.add(new Reader(ID_GENERATOR.incrementAndGet(), "Ivan Mazepa"));
  }

  public Optional<Reader> findById(long id){
    return readers.stream().filter(s -> s.getId() == id).findFirst();
  }

  public List<Reader> findAll(){
    return List.copyOf(readers);
  }

 public Reader save(Reader reader){
    reader.setId(ID_GENERATOR.incrementAndGet());
    readers.add(reader);
    return reader;
 }

}
