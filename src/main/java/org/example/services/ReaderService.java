package org.example.services;

import java.util.List;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;

public class ReaderService {

  private final ReaderRepository repository;

  public ReaderService(ReaderRepository repository) {
    this.repository = repository;
  }

  public void printReaders() {
  }

  public void addNewReader(String input){
  }

}
