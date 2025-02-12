package org.example.services;

import java.util.List;
import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.validator.ValidatorUtil;
import org.springframework.stereotype.Service;

import static org.example.validator.ValidatorUtil.validateInputOfId;

@Service
public class ReaderService {

  private final ReaderRepository readerRepository;

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

  public Optional<Reader> findById(Long id) {
    return readerRepository.findById(id);
  }

  public Reader addNewReader(Reader reader) {
    return readerRepository.save(reader);
  }
}
