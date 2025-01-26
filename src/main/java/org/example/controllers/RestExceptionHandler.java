package org.example.controllers;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.example.dto.ErrorResponseDTO;
import org.example.dto.ErrorResponseDTO.ErrorField;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class RestExceptionHandler {

  private final DateTimeFormatter dateTimeFormatter;

  public RestExceptionHandler(DateTimeFormatter dateTimeFormatter) {
    this.dateTimeFormatter = dateTimeFormatter;
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleInvalidField(BindingResult bindingResult) {
    return ResponseEntity.badRequest().body(mapToErrorResponseDTO(bindingResult));
  }

  private ErrorResponseDTO mapToErrorResponseDTO(BindingResult bindingResult) {
    return new ErrorResponseDTO(getFormatedDateTimeNow(), getDetailErrorMessage(bindingResult),
        bindingResult.getFieldErrors().stream().map(ErrorField::new).sorted(
            Comparator.comparing(ErrorField::getField)
        ).toList());
  }

  private String getDetailErrorMessage(BindingResult bindingResult) {
    return switch (bindingResult.getObjectName()) {
      case "newBookDTO" -> "Failed to create new book due to validation errors";
      default -> "Without details";
    };
  }

  private String getFormatedDateTimeNow() {
    return dateTimeFormatter.format(LocalDateTime.now());
  }

  @ExceptionHandler(value = HandlerMethodValidationException.class)
  public ResponseEntity<?> handleInvalidMethod(HandlerMethodValidationException exception) {
    return ResponseEntity.badRequest().body(exception.getAllValidationResults().stream()
        .map(ParameterValidationResult::getResolvableErrors).flatMap(List::stream).collect(
            Collectors.groupingBy(error -> "error",
                Collectors.mapping(MessageSourceResolvable::getDefaultMessage,
                    Collectors.toList()))));
  }

  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  public ResponseEntity<?> handleInvalidTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().body(getMismatchErrorMessage(ex));
  }

  private ErrorResponseDTO getMismatchErrorMessage(MethodArgumentTypeMismatchException ex) {
    var message = getErrorDetailFromMismatchException(ex);
    return new ErrorResponseDTO(getFormatedDateTimeNow(), message,
        List.of(new ErrorField(ex.getName(), ex.getValue().toString(), message)));
  }

  private String getErrorDetailFromMismatchException(MethodArgumentTypeMismatchException ex) {
    Function<MethodArgumentTypeMismatchException, String> errorFunction = exception -> {
      if (Long.class.equals(exception.getParameter().getParameterType())) {
        return "Parameter should contain only digits";
      }
      return exception.getMessage();
    };
    return errorFunction.apply(ex);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
    var date = getFormatedDateTimeNow();
    var response = ex.getConstraintViolations().stream().map(
            violation -> new ErrorResponseDTO(date, violation.getMessage(),
                List.of(ErrorField.mapErrorFieldFromConstraintViolationException(violation))))
        .findAny();
    return ResponseEntity.badRequest().body(response);
  }


}