package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.example.configuration.AppConfiguration;
import org.example.dto.ErrorResponseDTO;
import org.example.dto.NewBookDTO;
import org.example.entity.Book;
import org.example.services.BookService;
import static org.example.util.Util.getFirstBook;
import static org.example.util.Util.getResponseForIdZeroOrLess;
import static org.example.util.Util.getResponseForInvalidDecimalId;
import static org.example.util.Util.getResponseForInvalidFieldsInNewBookDTO;
import static org.example.util.Util.getTestBooks;
import static org.example.util.Util.setIdForTestBooks;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
@Import(AppConfiguration.class)
class BookControllerTest {

  private static final String REQUEST_PATH = "/api/v1/books";

  @Autowired
  MockMvc mvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  BookService bookService;
  @Autowired
  DateTimeFormatter dateTimeFormatter;

  @Test
  void shouldReturnNotFoundWhenBookNotPresent() throws Exception {
    var idTen = 10L;
    when(bookService.findById(idTen)).thenReturn(Optional.empty());
    mvc.perform(get(REQUEST_PATH + "/" + idTen)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBookWithId1() throws Exception {
    var idOne = 1L;
    when(bookService.findById(idOne)).thenReturn(Optional.of(getFirstBook()));
    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + idOne)).andExpect(status().isOk())
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var bookFromResponse = objectMapper.readValue(contentAsString, Book.class);

    assertAll(() -> assertEquals(idOne, bookFromResponse.getId()),
        () -> assertEquals("book", bookFromResponse.getName()),
        () -> assertEquals("book", bookFromResponse.getName()));

  }

  @ParameterizedTest
  @CsvSource({"title, author", "title valid, author valid",
      "Fahrenheit 451,  Ray Douglas Bradbury"})
  void shouldSaveNewBook(String title, String author) throws Exception {
    var bookDTO = new NewBookDTO(title, author);
    var book = new Book(bookDTO.title(), bookDTO.author());
    book.setId(5L);
    when(bookService.addNewBook(any(Book.class))).thenReturn(book);

    MvcResult mvcResult = mvc.perform(post(REQUEST_PATH).contentType("application/json")
        .content(objectMapper.writeValueAsString(bookDTO))).andExpect(status().is(201)).andReturn();

    var content = mvcResult.getResponse().getContentAsString();
    var bookFromResponse = objectMapper.readValue(content, Book.class);

    assertAll(() -> assertEquals(bookFromResponse.getId(), book.getId()),
        () -> assertEquals(bookDTO.author(), bookFromResponse.getAuthor()),
        () -> assertEquals(bookDTO.title(), bookFromResponse.getName()));

  }

  @ParameterizedTest
  @DisplayName("When send book with invalid fields should return 400 and error messages")
  @CsvSource({"tit#, !aut", "$$, 1"})
  void shouldReturnBadRequestWhenBookNotValid(String title, String author) throws Exception {
    var bookDTO = new NewBookDTO(title, author);
    var result = mvc.perform(post(REQUEST_PATH).contentType("application/json")
            .content(objectMapper.writeValueAsString(bookDTO))).andExpect(status().isBadRequest())
        .andReturn();

    var now = LocalDateTime.now().minusMinutes(1l);
    var expectedErrorResponseDTO = getResponseForInvalidFieldsInNewBookDTO(title, author);
    var errorResponseDTO = getErrorResponseFromMvcResult(result, objectMapper);

    assertAll(
        () -> assertEquals(4, errorResponseDTO.errors().size(), "Expected four errors in fields"),
        () -> assertTrue(errorResponseDTO.errors().containsAll(expectedErrorResponseDTO.errors()),
            "Some errors in fields miss"), () -> assertTrue(
            errorResponseDTO.errorMessage().contentEquals(expectedErrorResponseDTO.errorMessage()),
            "Error message is wrong"), () -> assertTrue(
            now.isBefore(LocalDateTime.parse(errorResponseDTO.date(), dateTimeFormatter)),
            "Time is missed"));

  }

  private ErrorResponseDTO getErrorResponseFromMvcResult(MvcResult result,
      ObjectMapper objectMapper) throws Exception {
    var content = result.getResponse().getContentAsString();
    return objectMapper.readValue(content, ErrorResponseDTO.class);

  }

  @Test
  void shouldReturnListOfBooks() throws Exception {
    when(bookService.findAllBooks()).thenReturn(setIdForTestBooks(getTestBooks()));
    mvc.perform(get(REQUEST_PATH))
        .andExpectAll(status().isOk(), jsonPath("$.length()", is(3)), jsonPath("$[0].id", is(1)),
            jsonPath("$[1].id", is(2)), jsonPath("$[2].id", is(3)),
            jsonPath("$[0].author", is("Test 1")), jsonPath("$[1].author", is("Test 2")),
            jsonPath("$[2].author", is("Test 3")));
  }

  @ParameterizedTest
  @CsvSource("{0, -5, -101}")
  void shouldReturnErrorWhenLessOrZeroValue(String id) throws Exception {

    var expectedErrorResponseDTO = getResponseForIdZeroOrLess(id);

    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + id))
        .andExpectAll(status().isBadRequest()).andReturn();

    var errorResponseDTO = getErrorResponseFromMvcResult(mvcResult, objectMapper);

    assertAll(() -> assertTrue(
            expectedErrorResponseDTO.errorMessage().contentEquals(errorResponseDTO.errorMessage())),
        () -> assertTrue(expectedErrorResponseDTO.errors().containsAll(errorResponseDTO.errors())),
        () -> assertEquals(1, errorResponseDTO.errors().size())
    );

  }

  @ParameterizedTest
  @CsvSource("{0.1, 1.5, 1.0}")
  void shouldReturnErrorWhenDecimal(String id) throws Exception {
    var expectedErrorResponseDTO = getResponseForInvalidDecimalId(id);

    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + id)).andExpect(status().isBadRequest()).andReturn();
    var errorResponseDTO = getErrorResponseFromMvcResult(mvcResult, objectMapper);

    assertAll(() -> assertTrue(
            expectedErrorResponseDTO.errorMessage().contentEquals(errorResponseDTO.errorMessage())),
        () -> assertTrue(expectedErrorResponseDTO.errors().containsAll(errorResponseDTO.errors())),
        () -> assertEquals(1, errorResponseDTO.errors().size())
    );

  }

}