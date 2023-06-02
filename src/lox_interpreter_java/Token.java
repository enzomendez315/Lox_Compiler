package lox_interpreter_java;

/*
 * Represents a token object.
 */
public class Token 
{
    final TokenType type;
    final String lexeme;
    final Object literal;
    final int line;

    /*
     * Constructs a Token oject.
     */
    public Token(TokenType type, String lexeme, Object literal, int line)
    {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /*
     * Converts the token to a string representation.
     */
    public String toString()
    {
        return type + " " + lexeme + " " + literal;
    }
}
