package lox_interpreter_java;

import java.util.List;

/*
 * This interface is used to call functions.
 */
public interface LoxCallable 
{
    public int arity();
    public Object call(Interpreter interpreter, List<Object> arguments);
}
