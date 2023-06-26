package lox_interpreter_java;

import java.util.List;

/*
 * This class is used to keep track of a function's state.
 */
public class LoxFunction implements LoxCallable
{
    private final Stmt.Function declaration;
    private final Environment closure;

    /*
     * Constructs a LoxFunction object.
     */
    public LoxFunction(Stmt.Function declaration, Environment closure)
    {
        this.declaration = declaration;
        this.closure = closure;
    }

    /*
     * Returns the number of parameters for the function.
     */
    @Override
    public int arity()
    {
        return declaration.params.size();
    }

    /*
     * Creates a new environment for the function and 
     * stores its state before executing its body.
     * 
     * If it catches a return exception, it pulls out 
     * the value from the exception and returns it.
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments)
    {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.params.size(); i++)
        {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        try
        {
            interpreter.executeBlock(declaration.body, environment);
        }
        catch (Return returnValue)
        {
            return returnValue.value;
        }

        return null;
    }

    /*
     * Returns a string representation of the function value.
     */
    @Override
    public String toString()
    {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
