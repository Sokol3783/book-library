package org.example.ui;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;


class BaseConsoleMenuTest {

    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeAll
    static void setUp() {
        System.setOut(new PrintStream(output));
    }

    @BeforeEach
    void setUpStream() {
        output.reset();
    }

    @Test
    @DisplayName("Menu should print welcome message only once")
    void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        String welcomeMessage = "WELCOME TO THE LIBRARY!";
        inputWithSleep("1", "2", "exit");
        String outputString= output.toString();
        long repeatTimes = Stream.of(output).map(ByteArrayOutputStream::toString).filter(s -> s.contains(welcomeMessage)).count();
        assertAll(() -> assertTrue(outputString.contains(welcomeMessage)),
                () -> assertEquals(1L, repeatTimes));
    }

    @Test
    @DisplayName("Menu should print three books after input '1'")
    void shouldPrintThreeBooksAfterStart() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        inputWithSleep("1");
        String outputString = output.toString();
        assertAll(() -> assertTrue(outputString.contains("Little prince")),
                () -> assertTrue(outputString.contains("Antoine de Saint-Exupéry")),
                () ->assertTrue(outputString.contains("Squealer")),
                () ->assertTrue(outputString.contains("George Orwell")),
                () ->assertTrue(outputString.contains("100 Years of Solitude")),
                () ->assertTrue(outputString.contains("Gabriel García Márquez")));

    }

    @Test
    @DisplayName("Menu should print three readers after input '2'")
    void shouldPrintThreeReadersAfterStart() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        inputWithSleep("2");
        String outputString = output.toString();
        assertAll(() -> assertTrue(outputString.contains("Kent Back")),
                () -> assertTrue(outputString.contains("Clark Kent")),
                () ->assertTrue(outputString.contains("Bruce Wayne")));
    }

    @Test
    @DisplayName("Menu shouldn't crash after incorrect input")
    void shouldNotFallDownAfterIncorrectInput() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        inputWithSleep("200");
        assertTrue(thread.isAlive());
        inputWithSleep("asdfasdf");
        assertTrue(thread.isAlive());
        inputWithSleep("2aghjasdhdjasdas0");
        assertTrue(thread.isAlive());
    }

    @Test
    @DisplayName("Close menu after input 'exit'")
    void shouldStopWorkingAfterInputExit() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        sleep(100);
        assertTrue(thread.isAlive());
        inputWithSleep("exit", "1", "2");
        String print = output.toString();
        assertAll(() -> assertFalse(print.contains("Kent Back"))
                , () -> assertFalse(print.contains("Clark Kent"))
                , () -> assertFalse(print.contains("George Orwell"))
                , () -> assertFalse(print.contains("Garcia Márquez"))
                );
    }

    @Test
    @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
    void shouldPrintGoodbyeAfterInputExit() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        inputWithSleep("exit");
        String outputString = output.toString();
        System.out.println(outputString);
        assertTrue(outputString.contains("Goodbye!"));
    }

    private void inputWithSleep(String... data) throws InterruptedException, IOException {
        for (String string : data ) {
            System.setIn(new ByteArrayInputStream(string.getBytes()));
            sleep(200);
        }
    }

}