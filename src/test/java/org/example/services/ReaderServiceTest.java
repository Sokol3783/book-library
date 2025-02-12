package org.example.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.example.dao.ReaderRepository;
import org.example.entity.Reader;
import org.example.exception.ConsoleValidationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.example.util.Util.getFistReader;
import static org.example.util.Util.getTestReaders;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Disabled
class ReaderServiceTest {

  @Mock
  private ReaderRepository readerRepository;
  @InjectMocks
  private ReaderService readerService;


  @Test
  void shouldReturnListOfReaders() {
    List<Reader> testReaders = getTestReaders();
    when(readerRepository.findAll()).thenReturn(testReaders);
    List<Reader> allReaders = readerService.findAllReaders();
    verify(readerRepository, times(1)).findAll();
    assertAll(
        () -> assertEquals(3, allReaders.size()),
        () -> assertTrue(testReaders.stream().allMatch(s -> s.getName().contains("Test")))
    );
  }

  @ParameterizedTest
  @MethodSource("provideInvalidNamesAndExpectedErrorMessage")
  void shouldThrowErrorAndMessageInvalidInput(String name, String message) {
    var exception = assertThrows(ConsoleValidationException.class,
        () -> readerService.addNewReader(name));
    assertTrue(exception.getMessage().contains(message));
  }

  private static Stream<Arguments> provideInvalidNamesAndExpectedErrorMessage() {
    return Stream.of(
        Arguments.of("abc", "Name should contain more than 5 chars and less than 30 ones"),
        Arguments.of("abcdefghjklmnopqrstuvwxyzabcdefgjklmnopqrstuvwxyz",
            "Name should contain more than 5 chars and less than 30 ones"),
        Arguments.of("#name", "Name must contain only letters, spaces, dashes, apostrophes"),
        Arguments.of("name#$!@", "Name must contain only letters, spaces, dashes, apostrophes"),
        Arguments.of("name&^&???", "Name must contain only letters, spaces, dashes, apostrophes")
    );
  }

  @Test
  void shouldReturnSavedReader() {
    var reader = getFistReader();
    when(readerRepository.save(any(Reader.class))).thenReturn(reader);
    var saved = readerService.addNewReader(getFistReader().getName());
    assertAll(
        () -> assertEquals(reader, saved),
        () -> assertEquals("reader", saved.getName())
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"1", "2", "3"})
  void shouldReturnBookIfValidInput(String id) {
    when(readerRepository.findById(anyLong())).thenReturn(Optional.of(new Reader("reader")));
    assertTrue(readerService.findById(id).isPresent());
  }

  @Test
  void shouldThrowValidationExceptionIfNotValidInput() {
    when(readerRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertAll(
        () -> assertThrows(ConsoleValidationException.class,
            () -> readerService.findById("asdasda")),
        () -> assertTrue(readerService.findById("1").isEmpty())
    );
  }


  @Test
  void shouldSaveNewBookFromEntity() {
    when(readerRepository.save(any(Reader.class))).thenReturn(getFistReader());
    var reader = new Reader("new reader");
    var saved = readerService.addNewReader(reader);
    assertNotEquals(0, saved.getId());
    verify(readerRepository, times(1)).save(reader);
  }

  @Test
  @DisplayName("Should find book by id when type is long and book present")
  void shouldFindByIdLong() {
    when(readerRepository.findById(1L)).thenReturn(Optional.of(getFistReader()));
    assertTrue(readerService.findById(1L).isPresent());
    verify(readerRepository, times(1)).findById(anyLong());
  }

  @Test
  @DisplayName("Should return empty optional when value not present")
  void shouldNotFindByIdLong() {
    when(readerRepository.findById(500L)).thenReturn(Optional.empty());
    assertTrue(readerService.findById(500L).isEmpty());
    verify(readerRepository, times(1)).findById(anyLong());
  }
}