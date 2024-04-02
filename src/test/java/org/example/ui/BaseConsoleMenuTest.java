package org.example.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.*;

import java.io.*;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

class BaseConsoleMenuTest {

    private static final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private static BaseConsoleMenu menu;

    @BeforeAll
    static void setUp() {
        System.setOut(new PrintStream(output));
    }

    private static void run() {
        System.err.println("RUN IN THREAD");
        menu = new BaseConsoleMenu();
        menu.run();
    }

    @BeforeEach
    void terminateThreadExecutor() {
        System.err.println("\nBEFORE EACH");
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void setInExitToMenu() {
        System.err.println("AFTER EACH\n");
        output.reset();
        executor.shutdownNow();
    }

    @Test
    @DisplayName("Menu should print welcome message only once")
    @Disabled
    void shouldPrintWelcomeMessageOnlyOnce() throws InterruptedException{
        executor.execute(BaseConsoleMenuTest::run);
        String welcomeMessage = "WELCOME TO THE LIBRARY!";
        inputWithSleep("1", "2", "exit");
        String outputString = output.toString();
        assertAll( () -> assertTrue(outputString.contains(welcomeMessage)),
                   () -> assertEquals(1,countRepeatedSubstrings(outputString, welcomeMessage)));
    }


    @Test
    @DisplayName("Menu should print three books after input '1'")
    void shouldPrintThreeBooksAfterInputOption_1() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
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
    void shouldPrintThreeReadersAfterInputOption_2() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep( "2");
        String outputString = output.toString();
        assertAll(() -> assertTrue(outputString.contains("Kent Back")),
                () -> assertTrue(outputString.contains("Clark Kent")),
                () ->assertTrue(outputString.contains("Bruce Wayne")));
    }

    @Test
    @DisplayName("Menu shouldn't crash after incorrect input")
    void shouldNotFallDownAfterIncorrectInput() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("200", "fdasdfhadjs", "asdasdfasdfasd");
        assertAll(() -> assertNotEquals(0,output.size()),
                  () -> assertFalse(menu.isTerminated()),
                  () -> assertEquals(4, countRepeatedSubstrings(output.toString(), getTextMenu())));
    }

    @Test
    @DisplayName("Close menu after input 'exit'")
    @Disabled
    void shouldStopWorkingAfterInputExit() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("exit", "1", "2");
        String outputString= output.toString();
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
    @Disabled
    @DisplayName("Menu should print 'Goodbye!' after input 'exit'")
    void shouldPrintGoodbyeAfterInputExit() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("exit");
        String outputString = output.toString();
        assertTrue(outputString.contains("Goodbye!"));
    }


    @Test
    @DisplayName("Menu should print list of option on startup")
    void shouldPrintListOfOptionOnStartup() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        sleep(100);
        assertTrue(output.toString().contains(getTextMenu()));
    }

    @Test
    @DisplayName("After any input should print menu except exit, after exit stop run menu")
    @Disabled
    void shouldPrintMenuAfterAnyInputExceptExit() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("1", "2", "3", "exit");
        assertEquals(4, countRepeatedSubstrings(output.toString(), getTextMenu()));
        assertTrue(menu.isTerminated());
    }

    @Test
    @DisplayName("After input 1 doesn't print another option and 'Goodbye'")
    void shouldNotPrintReadersAndExitAfterInput_1() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("1");
        String lines = output.toString();
        assertAll(() -> assertTrue(lines.contains("title")),
                  () -> assertFalse(lines.contains("Clark Kent")),
                  () -> assertFalse(lines.contains("Goodbye")));
    }

    @Test
    @DisplayName("After input 2 doesn't print 'Goodbye' ")
    void shouldNotPrintReadersAndExitAfterInput_2() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("2");
        String outputString = output.toString();
        assertAll(() -> assertFalse(outputString.contains("Goodbye")),
                  () -> assertTrue(outputString.contains("name")));
    }

    @Test
    @DisplayName("Books print from new line like: ID = **, author = **, title = **")
    void shouldPrintBooksInFormatFromNewLine() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("1");
        String lines= output.toString();
        assertAll( () -> assertEquals(3, countRepeatedSubstrings(lines, "ID")),
            () -> assertEquals(3,  countRepeatedSubstrings(lines, "title")),
            () -> assertEquals(3,  countRepeatedSubstrings(lines, "author")));
    }

    @Test
    @DisplayName("Readers print from new line like: ID = **, name = **")
    void shouldPrintReaderInFormatFromNewLine() throws InterruptedException {
        executor.execute(BaseConsoleMenuTest::run);
        inputWithSleep("2");
        String lines = output.toString();
        assertAll( () -> assertEquals(3, countRepeatedSubstrings(lines, "ID")),
                   () -> assertEquals(3,  countRepeatedSubstrings(lines, "name")));
    }

    private void inputWithSleep(String... data) throws InterruptedException {
        String join = String.join("\n", data);
        System.setIn(new ByteArrayInputStream(join.getBytes()));
        sleep(200);
    }

    private String getTextMenu() {
        return """
                PLEASE, SELECT ONE OF THE FOLLOWING ACTIONS BY TYPING THE OPTION’S NUMBER AND PRESSING ENTER KEY:
                [1] SHOW ALL BOOKS IN THE LIBRARY
                [2] SHOW ALL READERS REGISTERED IN THE LIBRARY
                """;
    }

    private static int countRepeatedSubstrings(String str, String target) {
        return (str.length() - str.replace(target, "").length()) / target.length();
    }
}