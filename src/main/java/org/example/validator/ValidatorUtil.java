package org.example.validator;

import java.util.regex.Pattern;
import org.example.exception.ConsoleValidationException;

public class ValidatorUtil {
  private static final String REG_EXP_TITLE_VALIDATION= "[|/#%=+*_><\\\\]";
  private static final String REG_EXP_NAME_VALIDATION= "[^\\w' -]";
  private static final Pattern TITLE_PATTERN = Pattern.compile(REG_EXP_TITLE_VALIDATION);
  private static final Pattern NAME_PATTERN = Pattern.compile(REG_EXP_NAME_VALIDATION);
  private static final Pattern NON_DIGIT_PATTERN = Pattern.compile("\\D");

  public static boolean invalidName(String name){
    return NAME_PATTERN.matcher(name).find();
  }

  public static boolean invalidTitle(String title){
    return TITLE_PATTERN.matcher(title).find();
  }

  public static boolean inRange(int current, int min, int max){
    return current >= min && current <= max;
  }

  public static void validateInputOfNewBook(String input) {
    StringBuilder message = new StringBuilder();
    String[] titleAndAuthor;
    if (inputContainsSingleSlash(input)){
      titleAndAuthor = input.split("/");
   } else {
      message.append("\nLine should contain singe dash");
      titleAndAuthor = new String[]{input, "\\"};
    }
    validateTitle(titleAndAuthor[0].strip(), message);
    validateNameOrAuthor(titleAndAuthor[1].strip(), "Author", message);

    if(!message.isEmpty()) throw new ConsoleValidationException(message.toString().strip());

  }

  public static void validateInputOfNewReader(String input) {
    StringBuilder message = new StringBuilder();
    validateNameOrAuthor(input, "Name", message);
    if(!message.isEmpty()) throw new ConsoleValidationException(message.toString().strip());
  }

  private static void validateNameOrAuthor(String input, String fieldName, StringBuilder message) {
    if (!inRange(input.length(), 5, 30)) message.append("Invalid length! ").append(fieldName)
        .append(" should contain more than 5 char and less than 30 ones");
    if (invalidName(input)) message.append(fieldName)
        .append(" must contain only letters, spaces, dashes, apostrophes!");
  }

  private static void validateTitle(String title, StringBuilder message) {
    if (!inRange(title.length(), 5, 100)) message.append("Invalid length! Title should contain more than 5 char and less than 100 ones");
    if (invalidTitle(title)) message.append("Title contains invalid symbols: |/\\\\#%=+*_><]");
  }

  public static boolean inputContainsSingleSlash(String input) {
    return input.chars().filter(s -> s == '\\').count() != 1;
  }

  public static void validateInputOfId(String input) {
    if (NON_DIGIT_PATTERN.matcher(input).find()) {
      throw new ConsoleValidationException("Line contains non digit symbols! Please enter only digits!");
    }
  }

}
