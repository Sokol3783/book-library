package org.example.controllers;


import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.example.dto.NewBookDTO;
import org.example.entity.Book;
import org.example.services.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @GetMapping
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.findAllBooks());
  }

  @PostMapping
  public ResponseEntity<?> saveBook(@RequestBody @Valid NewBookDTO newBookDTO,
      BindingResult bindingResult) {
    if (bindingResult.hasFieldErrors()) {
      return ResponseEntity.badRequest().
          body(bindingResult.getFieldErrors().stream().
              collect(Collectors.groupingBy(
                  FieldError::getField,
                  Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
              )));
    }
    var book = new Book(newBookDTO.title(), newBookDTO.author());
    var savedBook = bookService.addNewBook(book);
    return ResponseEntity.status(202).body(savedBook);
  }

  @GetMapping("{id}")
  public ResponseEntity<?> getBookById(@PathVariable(name = "id") Long id) {
    return bookService.findById(id).
        map(ResponseEntity::ok).
        orElse(ResponseEntity.notFound().build());
  }

}
