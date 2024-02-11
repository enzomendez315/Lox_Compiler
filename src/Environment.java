package lox_interpreter_java;

import java.util.HashMap;
import java.util.Map;

/*
 * This class stores the interpreter's variables 
 * and values.
 */
public class Environment 
{
    protected final Environment enclosing;
    private final Map<String, Object> values = new HashMap<>();

    /*
     * Constructs an Environment object for global variables.
     */
    public Environment()
    {
        enclosing = null;
    }

    /*
     * Constructs an Environment object using an enclosing 
     * for local variables.
     */
    public Environment(Environment enclosing)
    {
        this.enclosing = enclosing;
    }

    /*
     * Gets the value of the variable or returns an error 
     * if the variable is not defined.
     */
     public Object get(Token name)
    {
        if (values.containsKey(name.lexeme))
            return values.get(name.lexeme);

        if (enclosing != null)
            return enclosing.get(name);

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /*
     * Binds an existing variable name to a value, but 
     * does not create a new variable.
     */
    public void assign(Token name, Object value)
    {
        if (values.containsKey(name.lexeme))
        {
            values.put(name.lexeme, value);

            return;
        }

        if (enclosing != null)
        {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    /*
     * Binds a variable name to a value.
     */
    public void define(String name, Object value)
    {
        values.put(name, value);
    }

    /*
     * Returns the value of the variable in that 
     * environment's map.
     */
    public Object getAt(int distance, String name)
    {
        return ancestor(distance).values.get(name);
    }

    /*
     * Walks a fixed number of hops up the parent chain 
     * and puts the new value in the map.
     */
    public void assignAt(int distance, Token name, Object value)
    {
        ancestor(distance).values.put(name.lexeme, value);
    }

    /*
     * Walks a fixed number of hops up the parent chain 
     * and returns the environment there.
     */
    public Environment ancestor(int distance)
    {
        Environment environment = this;

        for (int i = 0; i < distance; i++)
        {
            environment = environment.enclosing;
        }

        return environment;
    }
}
