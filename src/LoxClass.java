package lox_interpreter_java;

import java.util.List;
import java.util.Map;

/*
 * This class is used to represent Lox classes.
 */
public class LoxClass implements LoxCallable 
{
    protected final String name;
    protected final LoxClass superclass;
    private final Map<String, LoxFunction> methods;

    /*
     * Constructs a LoxClass object.
     */
    public LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods)
    {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    /*
     * Returns the method of an instance's class. If the 
     * instance doesn't have it, it recurses up and looks 
     * for it in the superclass chain.
     */
    public LoxFunction findMethod(String name)
    {
        if (methods.containsKey(name))
            return methods.get(name);

        if (superclass != null)
            return superclass.findMethod(name);

        return null;
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
     * Then it looks for the initializer method and invokes 
     * it.
     */
    @Override
    public Object call(Interpreter interpreter, List<Object> arguments)
    {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod("init");

        if (initializer != null)
            initializer.bind(instance).call(interpreter, arguments);

        return instance;
    }

    /*
     * Returns the number of arguments passed.
     */
    @Override
    public int arity()
    {
        LoxFunction initializer = findMethod("init");

        if (initializer == null)
            return 0;
        
        return initializer.arity();
    }
}
