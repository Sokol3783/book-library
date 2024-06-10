package org.example.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class WelcomeController {

  private final static String WELCOME_MESSAGE = "Welcome to the library!";
  private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd.MM.yyyy HH:mm");

  @GetMapping("/welcome")
  public Map<String, String> getWelcomeMessage() {
    var map = new LinkedHashMap<String, String>();
    map.put("message", WELCOME_MESSAGE);
    map.put("currentDate", LocalDateTime.now().format(formatter));
    return map;
  }
}
