package org.example.exception;


public class RegistryRepositoryException extends RuntimeException {

  public RegistryRepositoryException() {
  }

  public RegistryRepositoryException(String message) {
    super(message);
  }

}
