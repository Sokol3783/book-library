package org.example.controllers;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.example.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BookController {

  @GetMapping("/books")
  public ResponseEntity<List<Book>> getAllBooks() {
    AtomicLong id = new AtomicLong(0);
    var books = new ArrayList<>(List.of(
        new Book("The Dark Tower", "Steven King"),
        new Book("The name of the Wind", "Patric Rotfuss"),
        new Book("A Game of Thrones", "George Martin")
    ));
    books.forEach(book -> book.setId(id.incrementAndGet()));
    return ResponseEntity.ok(books);
  }

  @PostMapping("/books")
  public ResponseEntity<Book> saveBook(@RequestBody BookDTO newBook) {
    var savedBook = new Book(newBook.title(), newBook.author());
    savedBook.setId(12345L);
    return ResponseEntity.ok(savedBook);
  }

  public record BookDTO(String title, String author) {

  }

}
