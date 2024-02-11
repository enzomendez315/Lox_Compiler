package lox_interpreter_java;

/*
 * This class is used to create runtime errors.
 */
public class RuntimeError extends RuntimeException
{
    final Token token;

    public RuntimeError(Token token, String message)
    {
        super(message);
        this.token = token;
    }
}
