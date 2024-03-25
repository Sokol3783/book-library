package org.example.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;


class BaseConsoleMenuTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final InputStream systemStream = System.in;
    private Thread thread;
    @BeforeEach
    void setUpStream() {
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void cleanUpStreams() {
        System.setOut(null);
        System.setIn(systemStream);
        output.reset();
        thread.interrupt();
    }

    @Test
    @DisplayName("Menu should print welcome message only once")
    void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException {
        startMenuInThread();
        String welcomeMessage = "WELCOME TO THE LIBRARY!";
        inputWithSleep("1", "2", "exit");
        String outputString= output.toString();
        long repeatTimes = Stream.of(output).map(ByteArrayOutputStream::toString).filter(s -> s.contains(welcomeMessage)).count();
        assertAll(() -> assertTrue(outputString.contains(welcomeMessage)),
                () -> assertEquals(1L, repeatTimes));
    }

    @Test
    @DisplayName("Menu should print three books after input '1'")
    void shouldPrintThreeBooksAfterStart() throws InterruptedException {
        startMenuInThread();
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
    void shouldPrintThreeReadersAfterStart() throws InterruptedException {
        startMenuInThread();
        inputWithSleep("2");
        String outputString = output.toString();
        assertAll(() -> assertTrue(outputString.contains("Kent Back")),
                () -> assertTrue(outputString.contains("Clark Kent")),
                () ->assertTrue(outputString.contains("Bruce Wayne")));
    }

    @Test
    @DisplayName("Menu shouldn't crash after incorrect input")
    void shouldNotFallDownAfterIncorrectInput(){

    }

    @Test
    @DisplayName("Close menu after input 'exit'")
    void shouldStopWorkingAfterInputExit(){

    }

    @Test
    @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
    void shouldPrintGoodbyeAfterInputExit(){

    }

    private void inputWithSleep(String... data) throws InterruptedException {
        for (String string : data ) {
            System.setIn(new ByteArrayInputStream(string.getBytes()));
            sleep(100);
        }
    }

    private void startMenuInThread() {
        BaseConsoleMenu menu = new BaseConsoleMenu();
        thread = new Thread(menu::run);
        thread.start();
    }

}