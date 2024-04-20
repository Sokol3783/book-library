package org.example.exception;

public class ConsoleValidationExceptionClass extends RuntimeException{

  public ConsoleValidationExceptionClass() {
    super();
  }

  public ConsoleValidationExceptionClass(String message) {
    super(message);
  }
}
