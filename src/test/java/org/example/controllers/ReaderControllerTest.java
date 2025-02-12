package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.example.configuration.DateTimeFormatterConfiguration;
import org.example.dto.NewReaderDTO;
import org.example.entity.Reader;
import org.example.services.ReaderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.example.util.Util.getErrorResponseFromMvcResult;
import static org.example.util.Util.getFistReader;
import static org.example.util.Util.getResponseForIdZeroOrLess;
import static org.example.util.Util.getResponseForInvalidDecimalId;
import static org.example.util.Util.getResponseForInvalidFieldsNewReaderDTO;
import static org.example.util.Util.getTestReaders;
import static org.example.util.Util.setIdForTestReaders;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WebMvcTest(ReaderController.class)
@Import(DateTimeFormatterConfiguration.class)
class ReaderControllerTest {

  private static final String REQUEST_PATH = "/api/v1/readers";

  @Autowired
  MockMvc mvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  ReaderService readerService;
  @Autowired
  DateTimeFormatter dateTimeFormatter;

  @Test
  void shouldReturnNotFoundWhenReaderNotPresent() throws Exception {
    var idTen = 10L;
    when(readerService.findById(idTen)).thenReturn(Optional.empty());
    mvc.perform(get(REQUEST_PATH + "/" + idTen)).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnReaderWithId1() throws Exception {
    var idOne = 1L;
    when(readerService.findById(idOne)).thenReturn(Optional.of(getFistReader()));
    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + idOne)).andExpect(status().isOk())
        .andReturn();

    var contentAsString = mvcResult.getResponse().getContentAsString();
    var readerFromResponse = objectMapper.readValue(contentAsString, Reader.class);

    assertAll(() -> assertEquals(idOne, readerFromResponse.getId()),
        () -> assertEquals("reader", readerFromResponse.getName()));

  }

  @ParameterizedTest
  @ValueSource(strings = {"Patric", "John Random", "Mike Douglas"})
  void shouldSaveNewReader(String name) throws Exception {
    var readerDTO = new NewReaderDTO(name);
    var reader = new Reader(name);
    reader.setId(5L);
    when(readerService.addNewReader(any(Reader.class))).thenReturn(reader);

    MvcResult mvcResult = mvc.perform(post(REQUEST_PATH).contentType("application/json")
            .content(objectMapper.writeValueAsString(readerDTO))).andExpect(status().is(201))
        .andReturn();

    var content = mvcResult.getResponse().getContentAsString();
    var readerFromResponse = objectMapper.readValue(content, Reader.class);

    assertAll(() -> assertEquals(readerFromResponse.getId(), reader.getId()),
        () -> assertEquals(name, readerFromResponse.getName()));

  }

  @ParameterizedTest
  @DisplayName("When send reader with invalid field should return 400 and error messages")
  @CsvSource({"!aut", "1", "$$"})
  void shouldReturnBadRequestWhenReaderNotValid(String name) throws Exception {
    var readerDTO = new NewReaderDTO(name);
    var result = mvc.perform(post(REQUEST_PATH).contentType("application/json")
            .content(objectMapper.writeValueAsString(readerDTO))).andExpect(status().isBadRequest())
        .andReturn();

    var now = LocalDateTime.now().minusMinutes(1L);
    var expectedErrorResponseDTO = getResponseForInvalidFieldsNewReaderDTO(name);
    var errorResponseDTO = getErrorResponseFromMvcResult(result, objectMapper);

    assertAll(
        () -> assertEquals(2, errorResponseDTO.errors().size(), "Expected two errors in fields"),
        () -> assertTrue(errorResponseDTO.errors().containsAll(expectedErrorResponseDTO.errors()),
            "Some errors in fields miss"),
        () -> assertTrue(
            errorResponseDTO.errorMessage().contentEquals(expectedErrorResponseDTO.errorMessage()),
            "Error message is wrong"),
        () -> assertTrue(
            now.isBefore(LocalDateTime.parse(errorResponseDTO.date(), dateTimeFormatter)),
            "Time is missed"));
  }


  @Test
  void shouldReturnListOfReaders() throws Exception {
    when(readerService.findAllReaders()).thenReturn(setIdForTestReaders(getTestReaders()));
    mvc.perform(get(REQUEST_PATH))
        .andExpectAll(status().isOk(), jsonPath("$.length()", is(3)), jsonPath("$[0].id", is(1)),
            jsonPath("$[1].id", is(2)), jsonPath("$[2].id", is(3)),
            jsonPath("$[0].name", is("Test 1")), jsonPath("$[1].name", is("Test 2")),
            jsonPath("$[2].name", is("Test 3")));
  }

  @ParameterizedTest
  @CsvSource({"0", "-5", "-101"})
  void shouldReturnErrorWhenLessOrZeroValue(String id) throws Exception {
    var expectedErrorResponseDTO = getResponseForIdZeroOrLess(id);
    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + id))
        .andExpectAll(status().isBadRequest()).andReturn();
    var errorResponseDTO = getErrorResponseFromMvcResult(mvcResult, objectMapper);

    assertAll(
        () -> assertTrue(
            expectedErrorResponseDTO.errorMessage().contentEquals(errorResponseDTO.errorMessage())),
        () -> assertTrue(expectedErrorResponseDTO.errors().containsAll(errorResponseDTO.errors())),
        () -> assertEquals(1, errorResponseDTO.errors().size())
    );

  }

  @ParameterizedTest
  @CsvSource({"0.1", "1.5", "1.0"})
  void shouldReturnErrorWhenDecimal(String id) throws Exception {
    var expectedErrorResponseDTO = getResponseForInvalidDecimalId(id);
    var mvcResult = mvc.perform(get(REQUEST_PATH + "/" + id)).andExpect(status().isBadRequest())
        .andReturn();
    var errorResponseDTO = getErrorResponseFromMvcResult(mvcResult, objectMapper);

    assertAll(
        () -> assertTrue(
            expectedErrorResponseDTO.errorMessage().contentEquals(errorResponseDTO.errorMessage())),
        () -> assertTrue(expectedErrorResponseDTO.errors().containsAll(errorResponseDTO.errors())),
        () -> assertEquals(1, errorResponseDTO.errors().size())
    );

  }

}