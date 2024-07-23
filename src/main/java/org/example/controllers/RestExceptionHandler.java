package org.example.controllers;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleInvalidField(BindingResult bindingResult) {
    return ResponseEntity.badRequest().
        body(bindingResult.getFieldErrors().stream().
            collect(Collectors.groupingBy(
                FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
            )));
  }

  @ExceptionHandler(value = HandlerMethodValidationException.class)
  public ResponseEntity<?> handleInvalidMethod(HandlerMethodValidationException exception) {
    return ResponseEntity.badRequest().body(exception.getAllValidationResults()
        .stream()
        .map(ParameterValidationResult::getResolvableErrors)
        .flatMap(List::stream)
        .collect(Collectors.groupingBy(error -> "error",
            Collectors.mapping(MessageSourceResolvable::getDefaultMessage, Collectors.toList())
        )));
  }

  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  public ResponseEntity<?> handleInvalidTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return ResponseEntity.badRequest().body(getMismatchErrorMessage(ex));
  }

  private Map<String, String> getMismatchErrorMessage(MethodArgumentTypeMismatchException ex) {
    Function<MethodArgumentTypeMismatchException, String> errorFunction = exception -> {
      if (Long.class.equals(exception.getParameter().getParameterType())) {
        return "Parameter should contain only digits";
      }
      return exception.getMessage();
    };
    return Map.of("error", errorFunction.apply(ex));
  }
}