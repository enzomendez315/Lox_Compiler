package lox_interpreter_java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/*
 * Represents a scripting language that is dynamically typed.
 */
public class Lox 
{
    static boolean hadError = false;

    public static void main(String[] args) throws IOException 
    {
        if (args.length > 1)
        {
            System.out.println("Usage: java lox [script]");
            System.exit(64);
        }
        else if (args.length == 1)
            runFile(args[0]);

        else 
            runPrompt();
    }

    /*
     * If the program is started from the command line and it is
     * given a path to a file, this method reads the file and
     * executes it.
     */
    private static void runFile(String path) throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // Indicate an error in the exit code.
        if (hadError)
            System.exit(65);
    }

    /*
     * Runs the program interatively. If the program is started 
     * without any arguments, this method will create a prompt 
     * where the user can enter and execute code one line
     * at a time.
     */
    private static void runPrompt() throws IOException
    {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true)
        {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    /*
     * Runs the program and starts scanning for Tokens.
     */
    private static void run(String source)
    {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens)
        {
            System.out.println(token);
        }
    }

    /*
     * Reports errors to the user.
     */
    static void error(int line, String message)
    {
        report(line, "", message);
    }

    /*
     * Prints out the report where the error is located and a message
     * explaining why.
     * This method is a helper for error().
     */
    private static void report(int line, String where, String message)
    {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}