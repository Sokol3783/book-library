package org.example.dao;

import java.util.List;
import org.example.entity.Book;
import org.example.entity.Reader;

public class RegistryRepository {


  public boolean borrowBook(Book book, Reader reader) {
    return false;
  }

  public boolean returnBook(Book book, Reader reader) {
    return false;
  }

  public Reader getReaderOfBook(Book book){
    return null;
  }

  public List<Book> getListBorrowedBooksOfReader(Reader reader){
    return List.of();
  }


}
