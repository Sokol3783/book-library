package org.example.dao;

import java.util.List;
import java.util.Optional;
import org.example.entity.Book;

public class BookRepository {

  public Optional<Book> findById(Long id){
    return Optional.empty();
  }

  public List<Book> findAllBooks(){
    return List.of();
  }

  public Book saveBook(Book book) {
    return null;
  }

}
