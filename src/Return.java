package lox_interpreter_java;

/*
 * This class is used to add RuntimeException functionality 
 * to the return value.
 */
public class Return extends RuntimeException
{
    protected final Object value;

    public Return(Object value)
    {
        super(null, null, false, false);
        this.value = value;
    }
}
