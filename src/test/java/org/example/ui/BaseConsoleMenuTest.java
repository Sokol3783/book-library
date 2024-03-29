package org.example.ui;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;


class BaseConsoleMenuTest {

    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @BeforeAll
    static void setUp() {
        System.setOut(new PrintStream(output));
    }

    @Test
    @DisplayName("Menu should print welcome message only once")
    void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException{
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        String welcomeMessage = "WELCOME TO THE LIBRARY!";
        inputWithSleep("1", "2", "exit");
        String outputString= output.toString();
        long repeatTimes = Stream.of(output).map(ByteArrayOutputStream::toString).filter(s -> s.contains(welcomeMessage)).count();
        assertAll(() -> assertTrue(outputString.contains(welcomeMessage)),
                () -> assertEquals(1L, repeatTimes));
        thread.interrupt();
    }



    @Test
    @DisplayName("Menu should print three books after input '1'")
    void shouldPrintThreeBooksAfterInputOption_1() throws InterruptedException {
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
        thread.interrupt();
    }

    @Test
    @DisplayName("Menu should print three readers after input '2'")
    void shouldPrintThreeReadersAfterInputOption_2() throws InterruptedException, IOException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        inputWithSleep("2");
        String outputString = output.toString();
        assertAll(() -> assertTrue(outputString.contains("Kent Back")),
                () -> assertTrue(outputString.contains("Clark Kent")),
                () ->assertTrue(outputString.contains("Bruce Wayne")));
        thread.interrupt();
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
        thread.interrupt();
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
        thread.interrupt();
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
        thread.interrupt();
    }

    private void inputWithSleep(String... data) throws InterruptedException{
        for (String string : data ) {
            System.setIn(new ByteArrayInputStream(string.getBytes()));
            sleep(200);
        }
    }

    @Test
    @DisplayName("Menu should print list of option on startup")
    void shouldPrintListOfOptionOnStartup(){
        output.reset();
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        assertTrue(output.toString().contains(getTextMenu()));
        thread.interrupt();
    }

    @Test
    @DisplayName("After input should print menu except exit")
    void shouldPrintMenuAfterAnyInputExceptExit() throws InterruptedException {
        Thread thread = new Thread(() -> new BaseConsoleMenu().run());
        thread.start();
        output.reset();
        inputWithSleep("1");
        assertTrue(output.toString().contains(getTextMenu()));
        output.reset();
        inputWithSleep("2");
        assertTrue(output.toString().contains(getTextMenu()));
        output.reset();
        inputWithSleep("3");
        assertTrue(output.toString().contains(getTextMenu()));
        output.reset();
        inputWithSleep("gfassdhjfhas");
        assertTrue(output.toString().contains(getTextMenu()));
        thread.interrupt();
    }

    private String getTextMenu() {
        return """
                PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:
                [1] SHOW ALL BOOKS IN THE LIBRARY
                [2] SHOW ALL READERS REGISTERED IN THE LIBRARY
                """;
    }

}