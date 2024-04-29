package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfId;

import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.validator.ValidatorUtil;

public class ReaderService {

  private final ReaderRepository repository;

  public ReaderService(ReaderRepository repository) {
    this.repository = repository;
  }

  public void printAllReaders() {
      System.out.println("Readers registered in library:");
      repository.findAll().forEach(System.out::println);
  }

  public void addNewReader(String input){

    ValidatorUtil.validateInputOfNewReader(input);
    Reader save = repository.save(new Reader(0l, input));
    System.out.println("Reader is registered in library:");
    System.out.println(save.toString());

  }

  public Optional<Reader> findById(String input) {
    validateInputOfId(input.strip());
    return repository.findById(Long.parseLong(input));
  }

}
