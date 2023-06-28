package lox_interpreter_java;

import java.util.List;
import java.util.Map;

/*
 * This class is used to represent Lox classes.
 */
public class LoxClass implements LoxCallable 
{
    protected final String name;

    /*
     * Constructs a LoxClass object.
     */
    public LoxClass(String name)
    {
        this.name = name;
    }

    /*
     * Returns a string representation of the class name.
     */
    @Override
    public String toString()
    {
        return name;
    }

    /*
     * Instantiates and returns a new LoxInstance object.
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments)
    {
        LoxInstance instance = new LoxInstance(this);

        return instance;
    }

    /*
     * Returns the number of arguments passed.
     */
    @Override
    public int arity()
    {
        return 0;
    }
}
