package lox_interpreter_java;

import java.util.List;

/*
 * This class is used to keep track of a function's state.
 */
public class LoxFunction implements LoxCallable
{
    private final Stmt.Function declaration;

    /*
     * Constructs a LoxFunction object.
     */
    public LoxFunction(Stmt.Function declaration)
    {
        this.declaration = declaration;
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
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments)
    {
        Environment environment = new Environment(interpreter.globals);
        for (int i = 0; i < declaration.params.size(); i++)
        {
            environment.define(declaration.params.get(i).lexeme, arguments.get(i));
        }

        interpreter.executeBlock(declaration.body, environment);

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
