package lox_interpreter_java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.util.ElementScanner6;

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
     * Checks if all the characters in the file have been processed.
     */
    private boolean isAtEnd()
    {
        return current >= source.length();
    }

    /*
     * Consumes the current character and selects a token type for it.
     */
    private void scanToken()
    {
        char character = advance();
        switch(character)
        {
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;

            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;

            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;

            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;

            case ',':
                addToken(TokenType.COMMA);
                break;

            case '.':
                addToken(TokenType.DOT);
                break;

            case '-':
                addToken(TokenType.MINUS);
                break;

            case '+':
                addToken(TokenType.PLUS);
                break;

            case ';':
                addToken(TokenType.SEMICOLON);
                break;

            case '*':
                addToken(TokenType.STAR);
                break;

            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;

            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;

            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;

            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;

            case '/':
                if (match('/'))
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                else 
                    addToken(TokenType.SLASH);
                break;
            
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            case '"':
                string();
                break;

            default:
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    /*
     * Takes the text of the current lexeme and creates a new token.
     */
    private void addToken(TokenType type)
    {
        addToken(type, null);
    }

    /*
     * Takes the text of the current lexeme and creates a new token.
     */
    private void addToken(TokenType type, Object literal)
    {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /*
     * Consumes and returns the next character.
     */
    private char advance()
    {
        return source.charAt(current++);
    }

    /*
     * Checks if the current character is the expected character.
     */
    private boolean match(char expected)
    {
        if (isAtEnd() || source.charAt(current) != expected)
            return false;
        
        current++;
        return true;
    }

    /*
     * Looks at the next unconsumed character and returns it.
     */
    private char peek()
    {
        if (isAtEnd())
            return '\0';

        return source.charAt(current);
    }

    private void string()
    {
        while (peek() != '"' && !isAtEnd())
        {
            if (peek() == '\n')
                line++;
            advance();
        }
        
        if (isAtEnd())
        {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source. substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }
}
