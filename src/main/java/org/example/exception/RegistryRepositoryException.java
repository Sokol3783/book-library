package org.example.exception;

public class RegistryRepositoryException extends RuntimeException {

  public RegistryRepositoryException() {
  }

  public RegistryRepositoryException(String message) {
    super(message);
  }

  public RegistryRepositoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public RegistryRepositoryException(Throwable cause) {
    super(cause);
  }

}
