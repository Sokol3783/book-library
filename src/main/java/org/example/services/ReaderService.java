package org.example.services;

import static org.example.validator.ValidatorUtil.hasNonDigitChar;
import static org.example.validator.ValidatorUtil.invalidName;

import java.util.Optional;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.exception.ConsoleValidationExceptionClass;
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

    if (!ValidatorUtil.inRange(input.length(), 5, 30)) {
      System.err.println("Invalid length of name\nName should contain more than 5 char and less than 30 ones");
      return;
    }

    if (invalidName(input)) {
      System.err.println("Name must contain only letters, spaces, dashes, apostrophes!");
      return;
    }

    Reader save = repository.save(new Reader(0l, input));
    System.out.println("Reader is registered in library:");
    System.out.println(save.toString());

  }
  public Optional<Reader> findById(String input) {
    if (hasNonDigitChar(input)) throw  new ConsoleValidationExceptionClass("Line contains non digit symbols! Please enter only digit!");
    return repository.findById(Long.parseLong(input));
  }
}
