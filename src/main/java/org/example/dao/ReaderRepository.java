package org.example.dao;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.example.entity.Reader;

public class ReaderRepository {

  private long lastId = 0;
  private final Set<Reader> readers= new TreeSet<>(Comparator.comparingLong(Reader::getId));

  public Optional<Reader> findById(long id){
    return readers.stream().filter(s -> s.getId() == id).findFirst();
  }

  public List<Reader> findAll(){
    return List.copyOf(readers);
  }

 public Reader save(Reader reader){
    if (reader.getId() == 0){
      reader.setId(getNextId());
    }
    readers.add(reader);
    return reader;
 }

  private long getNextId() {
    return ++lastId;
  }

}
