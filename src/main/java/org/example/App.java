package org.example;

import org.example.dao.BookRepository;
import org.example.dao.ReaderRepository;
import org.example.dao.RegistryRepository;
import org.example.entity.Book;
import org.example.entity.Reader;
import org.example.services.BookService;
import org.example.services.ReaderService;
import org.example.services.RegistryService;
import org.example.ui.AdvancedConsoleMenu;
import org.example.ui.ConsoleMenu;

public class App {

  public static void main(String[] args) {
    BookRepository bookRepository = new BookRepository();
    ReaderRepository readerRepository = new ReaderRepository();
    generateInitialBooksList(bookRepository);
    generateInitialReaderList(readerRepository);
    ConsoleMenu menu = new AdvancedConsoleMenu(new BookService(bookRepository)
                                              ,new ReaderService(readerRepository)
                                              ,new RegistryService(new RegistryRepository()));
      menu.run();
  }

  private static void generateInitialReaderList(ReaderRepository readerRepository) {
      readerRepository.save(new Reader(0, "Mike Douglas"));
      readerRepository.save(new Reader(0, "Fedor Trybeckoi"));
      readerRepository.save(new Reader(0, "Ivan Mazepa"));
  }

  private static void generateInitialBooksList(BookRepository bookRepository) {
    bookRepository.save(new Book(0l, "The Dark Tower", "Steven King"));
    bookRepository.save(new Book(0l, "The name of the Wind", "Patric Rotfuss"));
    bookRepository.save(new Book(0l, "A Game of Thrones", "George Martin"));
  }
}
