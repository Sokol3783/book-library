package org.example.validator;

import static java.lang.Long.parseLong;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.example.exception.ConsoleValidationException;

public class ValidatorUtil {

  private static final String REG_EXP_TITLE_VALIDATION = "[|/#%=+*_><\\\\]";
  private static final String REG_EXP_NAME_VALIDATION = "[^\\w' -]";
  private static final Pattern TITLE_PATTERN = Pattern.compile(REG_EXP_TITLE_VALIDATION);
  private static final Pattern NAME_PATTERN = Pattern.compile(REG_EXP_NAME_VALIDATION);
  private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("\\D");

  public static boolean invalidName(String name) {
    return NAME_PATTERN.matcher(name).find();
  }

  public static boolean invalidTitle(String title) {
    return TITLE_PATTERN.matcher(title).find();
  }

  private static boolean hasNonDigitSymbols(String input) {
    return NON_DIGIT_PATTERN.matcher(input).find();
  }

  public static boolean inRange(int current, int min, int max) {
    return current >= min && current <= max;
  }

  public static void validateInputOfNewBook(String input) {
    List<String> errors = new ArrayList<>();

    String[] titleAndAuthor = input.split("/");
    String title = titleAndAuthor[0].strip();
    String author = titleAndAuthor.length == 2 ? titleAndAuthor[1].strip() : "*";

    if (inputContainsSingleSlash(input)) {
      errors.add("Line should contain single dash");
    }
    if (!inRange(author.length(), 5, 30)) {
      errors.add("Invalid length. Name should contain more than 5 chars and less than 30 ones");
    }
    if (invalidName(author)) {
      errors.add("Name must contain only letters, spaces, dashes, apostrophes!");
    }
    if (!inRange(title.length(), 5, 100)) {
      errors.add("Invalid length. Title should contain more than 5 chars and less than 30 ones");
    }
    if (invalidTitle(title)) {
      errors.add("Title contains invalid symbols: |/\\\\#%=+*_><]");
    }

    String joinedErrors = errors.stream().collect(Collectors.joining(";"));
    if (!joinedErrors.isEmpty()) {
      throw new ConsoleValidationException(joinedErrors);
    }

  }

  public static void validateInputOfNewReader(String name) {
    List<String> errors = new ArrayList<>();

    if (!inRange(name.length(), 5, 30)) {
      errors.add("Invalid length. Name should contain more than 5 chars and less than 30 ones");
    }
    if (invalidName(name)) {
      errors.add("Name must contain only letters, spaces, dashes, apostrophes!");
    }

    String joinedErrors = errors.stream().collect(Collectors.joining(";"));
    if (!joinedErrors.isEmpty()) {
      throw new ConsoleValidationException(joinedErrors);
    }

  }

  public static boolean inputContainsSingleSlash(String input) {
    return input.chars().filter(s -> s == '/').count() != 1;
  }

  public static void validateInputOfId(String input) {
    String errors = validationOfId(input);
    if (!errors.isEmpty()) {
      throw new ConsoleValidationException(errors);
    }
  }

  private static String validationOfId(String input) {
    List<String> errors = new ArrayList<>();
    if (hasNonDigitSymbols(input)) {
      errors.add("Id contains non digit symbols! Please enter only digits!");
      errors.add("Id should be more than zero");
    } else {
      long id = parseLong(input);
      if (id < 1) {
        errors.add("Id should be more than zero");
      }
    }
    return errors.stream().collect(Collectors.joining(";"));
  }

  public static void validateInputOfTwoId(String input) {

    List<String> errors = new ArrayList<>();

    String[] bookIdAndReaderId = input.split("/");
    String bookId = bookIdAndReaderId[0].strip();
    String readerId = bookIdAndReaderId.length == 2 ? bookIdAndReaderId[1].strip() : "*";

    String bookIdErrors = validationOfId(bookId);
    String readerIdErrors = validationOfId(readerId);

    if (inputContainsSingleSlash(input)) {
      errors.add("Line should contain single dash");
    }
    if (!bookIdErrors.isEmpty()) {
      errors.add("\nInvalid book id:" + bookIdErrors);
    }
    if (!readerIdErrors.isEmpty()) {
      errors.add("\nInvalid reader id:" + readerIdErrors);
    }

    String joinedErrors = errors.stream().collect(Collectors.joining(";"));
    if (!joinedErrors.isEmpty()) {
      throw new ConsoleValidationException(joinedErrors);
    }

  }

}
