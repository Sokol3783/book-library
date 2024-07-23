package org.example.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.example.dto.NewBookDTO;
import org.example.entity.Book;
import org.example.services.BookService;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<?> saveBook(@RequestBody @Valid NewBookDTO newBookDTO) {
    var book = new Book(newBookDTO);
    var savedBook = bookService.addNewBook(book);
    return ResponseEntity.status(201).body(savedBook);
  }

  @GetMapping("{id}")
  public ResponseEntity<?> getBookById(@PathVariable(name = "id")
  @Min(value = 0L, message = "Min value should be 1")
  Long id) {
    return bookService.findById(id).
        map(ResponseEntity::ok).
        orElse(ResponseEntity.notFound().build());
  }

}
