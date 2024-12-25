package org.example.services;

import static org.example.validator.ValidatorUtil.validateInputOfId;

import java.util.List;
import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.validator.ValidatorUtil;

public class ReaderService {

  private final ReaderRepository readerRepository;

  public ReaderService() {
    this.readerRepository = new ReaderRepository(null);
  }

  public ReaderService(ReaderRepository readerRepository) {
    this.readerRepository = readerRepository;
  }

  public List<Reader> findAllReaders() {
    return readerRepository.findAll();
  }

  public Reader addNewReader(String input) {
    ValidatorUtil.validateInputOfNewReader(input);
    return readerRepository.save(new Reader(input));
  }

  public Optional<Reader> findById(String input) {
    validateInputOfId(input.strip());
    return readerRepository.findById(Long.parseLong(input));
  }

}
