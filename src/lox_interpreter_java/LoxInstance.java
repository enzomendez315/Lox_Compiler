package lox_interpreter_java;

import java.util.HashMap;
import java.util.Map;

/*
 * This class is used to represent a new instance 
 * of a Lox class.
 */
public class LoxInstance 
{
    private LoxClass klass;
    private final Map<String, Object> fields = new HashMap<>();

    /*
     * Constructs a LoxInstance object.
     */
    public LoxInstance(LoxClass klass)
    {
        this.klass = klass;
    }

    /*
     * Looks up and returns the value of a property if 
     * it exists or throws an error if it doesn't.
     */
    public Object get(Token name)
    {
        if (fields.containsKey(name.lexeme))
            return fields.get(name.lexeme);

        LoxFunction method = klass.findMethod(name.lexeme);

        if (method != null)
            return method;

        throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
    }

    /*
     * Stores the name and value of a field.
     */
    public void set(Token name, Object value)
    {
        fields.put(name.lexeme, value);
    }

    /*
     * Returns a string representation of a 
     * Lox class instance.
     */
    @Override
    public String toString()
    {
        return klass.name + " instance";
    }
}
