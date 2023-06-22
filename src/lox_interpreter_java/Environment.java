package lox_interpreter_java;

import java.util.HashMap;
import java.util.Map;

/*
 * This class stores the interpreter's variables 
 * and values.
 */
public class Environment 
{
    private final Map<String, Object> values = new HashMap<>();

    /*
     * Gets the value of the variable or returns an error 
     * if the variable is not defined.
     */
     public Object get(Token name)
    {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /*
     * Binds a variable name to a value.
     */
    public void define(String name, Object value)
    {
        values.put(name, value);
    }
}
