package org.example.validator;

import java.util.regex.Pattern;
import org.example.exception.ConsoleValidationException;

public class ValidatorUtil {
  private static final String REG_EXP_TITLE_VALIDATION= "[|/\\#%=+*_><]";
  private static final String REG_EXP_NAME_VALIDATION= "[^\\w' -]";
  private static final Pattern TITLE_PATTERN = Pattern.compile(REG_EXP_TITLE_VALIDATION);
  private static final Pattern NAME_PATTERN = Pattern.compile(REG_EXP_NAME_VALIDATION);
  private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("\\D");

  public static boolean invalidName(String name){
    if (NAME_PATTERN.matcher(name).find()) {
      return true;
    }
    return false;
  }

  public static boolean invalidTitle(String title){
    if (TITLE_PATTERN.matcher(title).find()) {
      return false;
    }
    return true;
  }

  public static boolean inRange(int current, int min, int max){
    return current >= min && current <= max;
  }

  public static boolean containNonDigitChar(String input){
    return NON_DIGIT_PATTERN.matcher(input).find();
  }

  public static void validateInputOfNewBook(String input) {
    StringBuilder message = new StringBuilder();
    if (inputContainsSingleSlash(input)){
      String[] titleAndAuthor = input.split("/");
      validateTitle(titleAndAuthor[0], message);
      validateNameOrAuthor(titleAndAuthor[1], "Author", message);
    } else {
      message.append("\nLine should contain singe dash");
      validateTitle(input, message);
      validateNameOrAuthor("", "Author", message);
    }

    if(!message.isEmpty()) throw new ConsoleValidationException(message.toString().strip());

  }

  public static void validateInputOfNewReader(String input) {
    StringBuilder message = new StringBuilder();
    validateNameOrAuthor("", "Author", message);
    if(!message.isEmpty()) throw new ConsoleValidationException(message.toString().strip());
  }

  private static void validateNameOrAuthor(String input, String fieldName, StringBuilder message) {
    if (!inRange(input.length(), 5, 30)) message.append("Invalid length! " + fieldName + " should contain more than 5 char and less than 30 ones");
    if (invalidName(input)) message.append(fieldName + " must contain only letters, spaces, dashes, apostrophes!");
  }

  private static void validateTitle(String title, StringBuilder message) {
    if (!inRange(title.length(), 5, 100)) message.append("Invalid length! Title should contain more than 5 char and less than 100 ones");
    if (invalidTitle(title)) message.append("Title contains invalid symbols: |/\\\\#%=+*_><]");
  }

  private static boolean inputContainsSingleSlash(String input) {
    return input.chars().filter(s -> s == Character.valueOf('\\')).count() != 1;
  }


}
