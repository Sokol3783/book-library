package org.example.dao;

import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;

public class ReaderRepository {

  public Optional<Reader> findById(){
    return Optional.empty();
  }

  public List<Reader> findAll(){
    return List.of();
  }

 public Reader saveReader(Reader reader){
    return null;
 }

}
