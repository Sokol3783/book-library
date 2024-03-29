package org.example.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.stream.Stream;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class BaseConsoleMenuTest {

    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    @BeforeAll
    static void setUp() {
        System.setOut(new PrintStream(output));
    }

    @BeforeEach
    void runMenu() throws InterruptedException {
        System.err.println("before each");
        output.reset();
        if (executor.isTerminated()) return;
        boolean b = executor.awaitTermination(1, TimeUnit.SECONDS);
        if (!b){
            executor.shutdownNow();
            executor = Executors.newSingleThreadExecutor();
        }
    }

    @AfterEach
    void setInExitToMenu() throws InterruptedException {
        System.err.println(output);
        System.err.println("set exit");
        inputWithSleep("exit");
    }

    @Test
    @DisplayName("Menu should print welcome message only once")
    void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException{
        executor.execute(() -> new BaseConsoleMenu().run());
        String welcomeMessage = "WELCOME TO THE LIBRARY!";
        inputWithSleep("1", "2", "exit");
        String outputString= output.toString();
        System.err.println(outputString);
        long repeatTimes = Stream.of(output).map(ByteArrayOutputStream::toString).filter(s -> s.contains(welcomeMessage)).count();
        assertAll(() -> assertTrue(outputString.contains(welcomeMessage)),
                () -> assertEquals(1L, repeatTimes));
    }

    @Test
    @DisplayName("Menu should print three books after input '1'")
    void shouldPrintThreeBooksAfterInputOption_1() throws InterruptedException {
        executor.execute(() -> new BaseConsoleMenu().run());
        inputWithSleep("1");
        String outputString = output.toString();
        System.err.println(outputString);
        assertAll(() -> assertTrue(outputString.contains("Little prince"), outputString),
                () -> assertTrue(outputString.contains("Antoine de Saint-Exupéry")),
                () ->assertTrue(outputString.contains("Squealer")),
                () ->assertTrue(outputString.contains("George Orwell")),
                () ->assertTrue(outputString.contains("100 Years of Solitude")),
                () ->assertTrue(outputString.contains("Gabriel García Márquez")));
    }

    @Test
    @DisplayName("Menu should print three readers after input '2'")
    void shouldPrintThreeReadersAfterInputOption_2() throws InterruptedException {
        executor.execute(() -> new BaseConsoleMenu().run());
        inputWithSleep("2");
        String outputString = output.toString();
        System.err.println(outputString);
        assertAll(() -> assertTrue(outputString.contains("Kent Back")),
                () -> assertTrue(outputString.contains("Clark Kent")),
                () ->assertTrue(outputString.contains("Bruce Wayne")));
    }

    @Test
    @DisplayName("Menu shouldn't crash after incorrect input")
    void shouldNotFallDownAfterIncorrectInput() throws InterruptedException {
        assertTrue(false);
        executor.execute(() -> new BaseConsoleMenu().run());
        inputWithSleep("200");
        inputWithSleep("asdfasdf");
        inputWithSleep("2aghjasdhdjasdas0");
    }

    @Test
    @DisplayName("Close menu after input 'exit'")
    void shouldStopWorkingAfterInputExit() throws InterruptedException {
        executor.execute(() -> new BaseConsoleMenu().run());
        inputWithSleep("exit", "1", "2");
        String outputString= output.toString();
        System.err.println(outputString);
        assertAll(
                () -> assertNotEquals(0, outputString.length()),
                () -> assertTrue(outputString.contains(getTextMenu())),
                () -> assertFalse(outputString.contains("Kent Back")),
                () -> assertFalse(outputString.contains("Clark Kent")),
                () -> assertFalse(outputString.contains("George Orwell")),
                () -> assertFalse(outputString.contains("Garcia Márquez"))
        );
    }

    @Test
    @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
    void shouldPrintGoodbyeAfterInputExit() throws InterruptedException {
        executor.execute(() -> new BaseConsoleMenu().run());
        inputWithSleep("exit");
        String outputString = output.toString();
        System.err.println(outputString);
        assertTrue(outputString.contains("Goodbye!"));
    }

    private void inputWithSleep(String... data) throws InterruptedException{
        for (String string : data ) {
            System.setIn(new ByteArrayInputStream(string.getBytes()));
            sleep(400);
        }
    }

    @Test
    @DisplayName("Menu should print list of option on startup")
    void shouldPrintListOfOptionOnStartup() throws InterruptedException {
        executor.execute(() -> new BaseConsoleMenu().run());
        System.err.println("Size of print "  + output.size());
        assertTrue(output.toString().contains(getTextMenu()));
    }

    @Test
    @DisplayName("After input should print menu except exit")
    void shouldPrintMenuAfterAnyInputExceptExit() throws InterruptedException {
        executor.execute(() -> new BaseConsoleMenu().run());
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
    }

    @Test
    @DisplayName("After input 1 doesn't print another option and 'Goodbye'")
    void shouldNotPrintReadersAndExitAfterInput_1() {
        assertFalse(true);
    }

    @Test
    @DisplayName("After input 2 doesn't print 'Goodbye' ")
    void shouldNotPrintReadersAndExitAfterInput_2() {
        assertFalse(true);
    }


    @Test
    @DisplayName("Books print from new line like: ID = **, author = **, title = **")
    void shouldPrintBooksInFormatFromNewLine(){
        assertFalse(true);
    }

    @Test
    @DisplayName("Readers print from new line like: ID = **, name = **")
    void shouldPrintReaderInFormatFromNewLine(){
        assertFalse(true);
    }

    private String getTextMenu() {
        return """
                PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:
                [1] SHOW ALL BOOKS IN THE LIBRARY
                [2] SHOW ALL READERS REGISTERED IN THE LIBRARY
                """;
    }

    void waitTerminationPreviousRun(ExecutorService executorService) throws InterruptedException {
        if ( executorService.isTerminated()) return;
        executorService.awaitTermination(3, TimeUnit.SECONDS);
    }
}