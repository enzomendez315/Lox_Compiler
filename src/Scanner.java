package lox_interpreter_java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int line = 1;       // Source line where 'current' is on.

    private static final Map<String, TokenType> keywords;

    static
    {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

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
                if (isDigit(character))
                    number();
                else if (isAlpha(character))
                    identifier();
                else
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

    /*
     * Looks ahead two characters and returns whatever character
     * it encounters.
     */
    private char peekNext()
    {
        if (current + 1 >= source.length())
            return '\0';
        
        return source.charAt(current + 1);
    }

    /*
     * Checks if the current character is a number from 0 to 9.
     */
    private boolean isDigit(char character)
    {
        return character >= '0' && character <= '9';
    }

    /*
     * Checks if the character is a letter or a "_".
     */
    private boolean isAlpha(char character)
    {
        return (character >= 'a' && character <= 'z') || 
                (character >= 'A' && character <= 'Z') || 
                character == '_';
    }

    /*
     * Checks if the character is alphanumeric.
     */
    private boolean isAlphaNumeric(char character)
    {
        return isAlpha(character) || isDigit(character);
    }

    /*
     * Checks if the next character is part of the string and adds
     * it as a string token.
     * 
     * It also checks if the string is unterminated and sends an error.
     */
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

        // For the closing ".
        advance();

        // Trim the surrounding quotes.
        String value = source. substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    /*
     * Scans the number and consumes it. Then adds it as a number token.
     * It consumes as many digits as it finds for the integer part of 
     * the literal, then it looks for a fractional part (separated by 
     * a decimal point) and does the same for the decimals.
     */
    private void number()
    {
        while (isDigit(peek()))
            advance();
        
        // Check if number has decimals.
        if (peek() == '.' && isDigit(peekNext()))
            // Consume the "."
            advance();

        while (isDigit(peek()))
            advance();
        
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /*
     * Checks if the character is alphanumeric and advances until
     * the character is neither a digit nor a letter. 
     * 
     * If the substring is found on the keywords hashmap, it is added
     * as that token's type. Otherwise, it is added as a user-defined
     * identifier token.
     */
    private void identifier()
    {
        while (isAlphaNumeric(peek()))
            advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
    
        if (type == null)
            type = TokenType.IDENTIFIER;
    
        addToken(type);
    }
}
