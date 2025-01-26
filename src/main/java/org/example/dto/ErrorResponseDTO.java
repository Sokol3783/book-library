package org.example.dto;

import jakarta.validation.ConstraintViolation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.validation.FieldError;

public record ErrorResponseDTO(String date, String errorMessage, List<ErrorField> errors) {

  public static class ErrorField {

    private final String field;
    private final String invalidValue;
    private final String error;

    public ErrorField() {
      this.field = "";
      this.invalidValue = "";
      this.error = "";
    }

    public ErrorField(String field, String invalidValue, String error) {
      this.field = field;
      this.invalidValue = invalidValue;
      this.error = error;
    }

    public ErrorField(FieldError fieldError) {
      this.field = fieldError.getField();
      this.invalidValue = Optional.ofNullable(fieldError.getRejectedValue()).map(Object::toString)
          .orElse("unknown value");
      this.error = fieldError.getDefaultMessage();
    }

    public static ErrorField mapErrorFieldFromConstraintViolationException(
        ConstraintViolation<?> violation) {
      return new ErrorField(getNameFieldFromConstraintViolationException(violation),
          violation.getInvalidValue().toString(), violation.getMessage());
    }

    private static String getNameFieldFromConstraintViolationException(
        ConstraintViolation<?> violation) {

      if (violation.getPropertyPath().toString().contains("getBookById")) {
        return "id";
      }
      return "unknown field";
    }

    public String getField() {
      return field;
    }

    public String getInvalidValue() {
      return invalidValue;
    }

    public String getError() {
      return error;
    }

    @Override
    public final boolean equals(Object o) {
      if (!(o instanceof ErrorField that)) {
        return false;
      }

      return Objects.equals(field, that.field) && Objects.equals(invalidValue,
          that.invalidValue) && Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {
      int result = Objects.hashCode(field);
      result = 31 * result + Objects.hashCode(invalidValue);
      result = 31 * result + Objects.hashCode(error);
      return result;
    }
  }
}
