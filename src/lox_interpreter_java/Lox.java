package lox_interpreter_java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.lang.model.util.ElementScanner6;

public class Lox 
{
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

    private static void runFile(String path) throws IOException
    {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws IOException
    {

    }

    private static void run(String source)
    {

    }
}