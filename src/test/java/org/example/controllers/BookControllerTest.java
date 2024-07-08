package org.example.controllers;

import static org.example.util.Util.getFirstBook;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.example.controllers.BookController.BookDTO;
import org.example.entity.Book;
import org.example.services.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
class BookControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  BookService bookService;

  @Test
  void shouldReturnNotFoundWhenBookNotPresent() throws Exception {
    when(bookService.findById(10L)).thenReturn(Optional.empty());
    mvc.perform(get("book-library/api/v1/books/10")).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnFirstBook() throws Exception {
    when(bookService.findById(1L)).thenReturn(Optional.of(getFirstBook()));
    var mvcResult = mvc.perform(get("/book-library/api/v1/books/1")).andExpect(status().isOk())
        .andReturn();
    var contentAsString = mvcResult.getResponse().getContentAsString();
    var bookFromResponse = objectMapper.readValue(contentAsString, Book.class);

    assertAll(() -> assertEquals(1L, bookFromResponse.getId()),
        () -> assertEquals("book", bookFromResponse.getName()),
        () -> assertEquals("book", bookFromResponse.getName()));

  }

  @Test
  void shouldSaveNewBook() throws Exception {
    var bookDTO = new BookDTO("title", "author");
    var book = new Book(bookDTO.title(), bookDTO.author());
    book.setId(5L);
    when(bookService.addNewBook(any(Book.class))).thenReturn(book);

    MvcResult mvcResult = mvc.perform(
            post("/book-library/api/v1/book").
                contentType("application/json").
                content(objectMapper.writeValueAsString(bookDTO)))
        .andExpect(status().isOk())
        .andReturn();

    var content = mvcResult.getResponse().getContentAsString();
    var bookFromResponse = objectMapper.readValue(content, Book.class);

    assertAll(
        () -> assertEquals(bookFromResponse.getId(), book.getId()),
        () -> assertEquals(bookDTO.author(), bookFromResponse.getAuthor()),
        () -> assertEquals(bookDTO.title(), bookFromResponse.getName())
    );

  }

  @Test
  @DisplayName("When send book with invalid fields should return 400 and error messages")
  void shouldReturnBadRequestWhenBookNotValid() throws Exception {
    var bookDTO = new BookDTO("tit", "auh");
    mvc.perform(
            post("/book-library/api/v1/book").
                contentType("application/json").
                content(objectMapper.writeValueAsString(bookDTO)))
        .andExpectAll(status().isBadRequest(),
            jsonPath("$.title", notNullValue()),
            jsonPath("$.author", notNullValue()));

  }

  @Test
  void shouldReturnListOfBooks() throws Exception {
    when(bookService.findAllBooks()).thenReturn(setIdForTestBooks(getTestBooks()));
    mvc.perform(get("/book-library/api/v1/books"))
        .andExpectAll(status().isOk(),
            jsonPath("$.length()", hasSize(3)),
            jsonPath("$[0].id", is(1)),
            jsonPath("$[1].id", is(2)),
            jsonPath("$[2].id", is(3))
        );
  }

}