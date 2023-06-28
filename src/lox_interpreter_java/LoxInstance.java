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

    /*
     * Constructs a LoxInstance object.
     */
    public LoxInstance(LoxClass klass)
    {
        this.klass = klass;
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
