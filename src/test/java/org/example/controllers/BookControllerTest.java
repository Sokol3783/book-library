package org.example.controllers;

import static org.example.util.Util.getFirstBook;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
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
import org.example.dto.NewBookDTO;
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

  private static final String REQUEST_PATH = "/api/v1/books";

  @Autowired
  MockMvc mvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  BookService bookService;

  @Test
  void shouldReturnNotFoundWhenBookNotPresent() throws Exception {
    var idTen = 10L;
    when(bookService.findById(idTen)).thenReturn(Optional.empty());
    mvc.perform(get(REQUEST_PATH + "/" + idTen)).
        andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBookWithId1() throws Exception {
    var idOne = 1L;
    when(bookService.findById(idOne)).thenReturn(Optional.of(getFirstBook()));
    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + idOne)).
        andExpect(status().isOk()).
        andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var bookFromResponse = objectMapper.readValue(contentAsString, Book.class);

    assertAll(() -> assertEquals(idOne, bookFromResponse.getId()),
        () -> assertEquals("book", bookFromResponse.getName()),
        () -> assertEquals("book", bookFromResponse.getName()));

  }

  @Test
  void shouldSaveNewBook() throws Exception {
    var bookDTO = new NewBookDTO("title", "author");
    var book = new Book(bookDTO.title(), bookDTO.author());
    book.setId(5L);
    when(bookService.addNewBook(any(Book.class))).thenReturn(book);

    MvcResult mvcResult = mvc.perform(
            post(REQUEST_PATH).
                contentType("application/json").
                content(objectMapper.writeValueAsString(bookDTO)))
        .andExpect(status().is(202))
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
    var bookDTO = new NewBookDTO("tit#", "!auh");
    mvc.perform(
            post(REQUEST_PATH).
                contentType("application/json").
                content(objectMapper.writeValueAsString(bookDTO)))
        .andExpectAll(status().isBadRequest(),
            jsonPath("$.title", containsInAnyOrder(
                "Invalid length. Title should contain more than 5 chars and less than 100 ones",
                "Title contains invalid symbols: |/\\#%=+*_><]")),
            jsonPath("$.author", containsInAnyOrder(
                "Invalid length. Name should contain more than 5 chars and less than 30 ones",
                "Name must contain only letters, spaces, dashes, apostrophes!")));


  }

  @Test
  void shouldReturnListOfBooks() throws Exception {
    when(bookService.findAllBooks()).thenReturn(setIdForTestBooks(getTestBooks()));
    mvc.perform(get(REQUEST_PATH))
        .andExpectAll(status().isOk(),
            jsonPath("$.length()", is(3)),
            jsonPath("$[0].id", is(1)),
            jsonPath("$[1].id", is(2)),
            jsonPath("$[2].id", is(3)),
            jsonPath("$[0].author", is("Test 1")),
            jsonPath("$[1].author", is("Test 2")),
            jsonPath("$[2].author", is("Test 3"))
        );
  }

  @Test
  void shouldReturnErrorWhenLessOrZeroValue() throws Exception {

    mvc.perform(get(REQUEST_PATH + "/" + "-5"))
        .andExpectAll(
            status().isBadRequest(),
            jsonPath("$.error").value("Min value should be 1")
        );

  }

  @Test
  void shouldReturnErrorWhenDecimal() throws Exception {
    mvc.perform(get(REQUEST_PATH + "/" + "1.1"))
        .andExpectAll(
            status().isBadRequest(),
            jsonPath("$.error").value("Parameter should contain only digits"));
  }


}