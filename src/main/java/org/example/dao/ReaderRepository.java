package org.example.dao;

import java.util.List;
import java.util.Optional;
import org.example.entity.Reader;

public class ReaderRepository {

  public Optional<Reader> findById(long id){
    return Optional.empty();
  }

  public List<Reader> findAll(){
    return List.of();
  }

 public Reader save(Reader reader){
    return null;
 }

}
