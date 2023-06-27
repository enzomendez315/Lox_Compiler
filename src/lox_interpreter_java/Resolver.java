package lox_interpreter_java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/*
 * This class is used to visit every node in a syntax tree to 
 * optimize variable resolution.
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>
{
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    /*
     * Constructs a Resolver object.
     */
    public Resolver(Interpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    /*
     * Begins a new scope, traverses into the statements inside 
     * the block, and then discards the scope.
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt)
    {
        beginScope();
        resolve(stmt.statements);
        endScope();

        return null;
    }

    /*
     * Declares and defines a variable statement.
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt)
    {
        declare(stmt.name);

        if (stmt.initializer != null)
            resolve(stmt.initializer);

        define(stmt.name);

        return null;
    }

    /*
     * Takes a list of statements and resolves each one.
     */
    public void resolve(List<Stmt> statements)
    {
        for (Stmt statement : statements)
        {
            resolve(statement);
        }
    }

    /*
     * Applies the Visitor pattern to the given 
     * syntax tree node.
     */
    private void resolve(Stmt stmt)
    {
        stmt.accept(this);
    }

    /*
     * Applies the Visitor pattern to the given 
     * syntax tree node.
     */
    private void resolve(Expr expr)
    {
        expr.accept(this);
    }

    /*
     * Adds a new scope to the stack.
     */
    private void beginScope()
    {
        scopes.push(new HashMap<String, Boolean>());
    }

    /*
     * Removes the innermost scope.
     */
    private void endScope()
    {
        scopes.pop();
    }

    /*
     * Adds a variable to the innermost scope so that it 
     * shadows any outer one. Then it binds the variable 
     * to 'false' to represent that the variable's initializer 
     * hasn't been resolved.
     */
    private void declare(Token name)
    {
        if (scopes.isEmpty())
            return;

        Map<String, Boolean> scope = scopes.peek();
        scope.put(name.lexeme, false);
    }

    /*
     * Sets the variable's value in the scope map to true 
     * to mark it as fully initialized and ready to use.
     */
    private void define(Token name)
    {
        if (scopes.isEmpty())
            return;
        
        scopes.peek().put(name.lexeme, true);
    }
}
