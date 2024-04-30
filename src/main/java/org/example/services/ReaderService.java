package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfId;

import java.util.List;
import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.validator.ValidatorUtil;

public class ReaderService {

  private final ReaderRepository repository;

  public ReaderService(ReaderRepository repository) {
    this.repository = repository;
  }

  public List<Reader> findAllReaders() {
      return repository.findAll();
  }

  public Reader addNewReader(String input){
    ValidatorUtil.validateInputOfNewReader(input);
    return repository.save(new Reader(0l, input));
  }

  public Optional<Reader> findById(String input) {
    validateInputOfId(input.strip());
    return repository.findById(Long.parseLong(input));
  }

}
