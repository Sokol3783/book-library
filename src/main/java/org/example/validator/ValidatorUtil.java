package org.example.validator;

import java.util.regex.Pattern;

public class ValidatorUtil {
  private static final String REG_EXP_TITLE_VALIDATION= "[|/\\#%=+*_><]";
  private static final String REG_EXP_NAME_VALIDATION= "[^\\w' -]";
  private static final Pattern TITLE_PATTERN = Pattern.compile(REG_EXP_TITLE_VALIDATION);
  private static final Pattern NAME_PATTERN = Pattern.compile(REG_EXP_NAME_VALIDATION);

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
    if ((current < min) || (current > max)) return false;
    return true;
  }
}
