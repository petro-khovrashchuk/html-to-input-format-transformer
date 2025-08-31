package com.petrokhovrashchuk.petprojects.exception;

public class DocumentNotLoadedException extends RuntimeException {

  public DocumentNotLoadedException() {
  }

  public DocumentNotLoadedException(String message) {
    super(message);
  }

  public DocumentNotLoadedException(String message, Throwable cause) {
    super(message, cause);
  }
}
