package org.example.exception;

public class ConsoleValidationException extends RuntimeException {

  public ConsoleValidationException() {
    super();
  }

  public ConsoleValidationException(String message) {
    super(message);
  }
}
