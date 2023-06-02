package lox_interpreter_java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static lox_interpreter_java.TokenType.*;

/*
 * Scans characters and figures out what lexeme the character
 * belongs to.
 * Emits a token when it reaches the end of that lexeme.
 */
public class Scanner 
{
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;      // First character in the lexeme being scanned.
    private int current = 0;    // Character that is currently being processed.
    private int line = 1;       // Source line where current is on.

    /*
     * Constructs a Scanner object.
     */
    public Scanner(String source)
    {
        this.source = source;
    }

    /*
     * Scans the source code and adds tokens to the list until 
     * it runs out of characters. Then it appends one final "end 
     * of file" token.
     */
    public List<Token> scanTokens()
    {
        while (!isAtEnd())
        {
            // This is the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    /*
     * Helper method that checks if all the characters in the file
     * have been checked.
     */
    private boolean isAtEnd()
    {
        return current >= source.length();
    }

    /*
     * 
     */
    private void scanToken()
    {

    }
}
