package lox_interpreter_java;

import java.util.ArrayList;
import java.util.List;

/*
 * This class is used to evaluate expressions and produce values 
 * using the syntax trees created by the parser.
 */
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>
{
    protected final Environment globals = new Environment();
    private Environment environment = globals;

    /*
     * Constructs an Interpreter object and defines its 
     * native functions.
     */
    public Interpreter()
    {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity()
            {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments)
            {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString()
            {
                return "<native fn>";
            }
        });
    }

    /*
     * Takes in a series of statements and evaluates them.
     * If a runtime error is thrown, it is caught and dealt with.
     */
    public void interpret(List<Stmt> statements)
    {
        try
        {
            for (Stmt statement : statements)
            {
                execute(statement);
            }
        } 
        catch (RuntimeError error)
        {
            Lox.runtimeError(error);
        }
    }

    /*
     * Evaluates a binary expression and returns the result.
     */
    @Override
    public Object visitBinaryExpr(Expr.Binary expr) 
    {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double)left > (double)right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left >= (double)right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left < (double)right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double)left <= (double)right;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double)
                    return (double)left + (double)right;

                if (left instanceof String && right instanceof String)
                    return (String)left + (String)right;

                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }

        return null;
    }

    /*
     * Evaluates expression for callee and then for each of 
     * the arguments in order. Then it calls the function of 
     * the callee.
     */
    @Override
    public Object visitCallExpr(Expr.Call expr)
    {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for (Expr argument : expr.arguments)
        {
            arguments.add(evaluate(argument));
        }

        if (!(callee instanceof LoxCallable))
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");

        LoxCallable function = (LoxCallable)callee;

        if (arguments.size() != function.arity())
            throw new RuntimeError(expr.paren, "Expected " + function.arity() + " arguments but got " + arguments.size() + ".");
        
        return function.call(this, arguments);
    }

    /*
     * Evaluates a grouping expression and returns the result.
     */
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) 
    {
        return evaluate(expr.expression);
    }

    /*
     * Evaluates a literal expression and returns the result.
     */
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) 
    {
        return expr.value;
    }

    /*
     * Evaluates left operand first to see if it meets conditions 
     * for 'and'/'or' statements. Then evaluates the right operand.
     */
    @Override
    public Object visitLogicalExpr(Expr.Logical expr)
    {
        Object left = evaluate(expr.left);

        if (expr.operator.type == TokenType.OR)
        {
            if (isTruthy(left))
                return left;
        }
        else
        {
            if (!isTruthy(left))
                return left;
        }

        return evaluate(expr.right);
    }

    /*
     * Evaluates a unary expression and returns the result.
     */
    @Override
    public Object visitUnaryExpr(Expr.Unary expr) 
    {
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
        }

        return null;
    }

    /*
     * Evaluates a variable expression and returns the result.
     */
    @Override
    public Object visitVariableExpr(Expr.Variable expr)
    {
        return environment.get(expr.name);
    }

    /*
     * Evaluates an expression.
     */
    private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }

    /*
     * Executes a statement.
     */
    private void execute(Stmt stmt)
    {
        stmt.accept(this);
    }

    /*
     * Executes each statement in a block and 
     * sets a new environment for the scope.
     */
    public void executeBlock(List<Stmt> statements, Environment environment)
    {
        Environment previous = this.environment;
        try
        {
            this.environment = environment;

            for (Stmt statement : statements)
            {
                execute(statement);
            }
        }
        finally
        {
            this.environment = previous;
        }
    }

    /*
     * Evaluates an entire block or scope.
     */
    @Override
    public Void visitBlockStmt(Stmt.Block stmt)
    {
        executeBlock(stmt.statements, new Environment(environment));

        return null;
    }

    /*
     * Evaluates an expression statement and returns the result.
     */
    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt)
    {
        evaluate(stmt.expression);

        return null;
    }

    /*
     * Evaluates a function declaration and binds the resulting 
     * object to a new variable.
     */
    @Override
    public Void visitFunctionStmt(Stmt.Function stmt)
    {
        LoxFunction function = new LoxFunction(stmt, environment);
        environment.define(stmt.name.lexeme, function);

        return null;
    }

    /*
     * Evaluates a condition and executes the 'then' 
     * branch if true. Executes the 'else' branch if false.
     */
    @Override
    public Void visitIfStmt(Stmt.If stmt)
    {
        if (isTruthy(evaluate(stmt.condition)))
            execute(stmt.thenBranch);
        else if (stmt.elseBranch != null)
            execute(stmt.elseBranch);

        return null;
    }

    /*
     * Prints an expression statement.
     */
    @Override
    public Void visitPrintStmt(Stmt.Print stmt) 
    {
       Object value = evaluate(stmt.expression);
       System.out.println(stringify(value));

       return null;
    }

    /*
     * Evaluates the return value or throws an exception 
     * if there isn't one.
     */
    @Override
    public Void visitReturnStmt(Stmt.Return stmt)
    {
        Object value = null;
        if (stmt.value != null)
            value = evaluate(stmt.value);

        throw new Return(value);
    }

    /*
     * Evaluates a variable statement.
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt)
    {
        Object value = null;
        if (stmt.initializer != null)
            value = evaluate(stmt.initializer);

        environment.define(stmt.name.lexeme, value);

        return null;
    }

    /*
     * Executes a while statement.
     */
    @Override
    public Void visitWhileStmt(Stmt.While stmt)
    {
        while (isTruthy(evaluate(stmt.condition)))
        {
            execute(stmt.body);
        }

        return null;
    }

    /*
     * Evaluates an assignment expression.
     */
    @Override
    public Object visitAssignExpr(Expr.Assign expr)
    {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);

        return value;
    }

    /*
     * Checks that the operand is a number on which the operator
     * can be used.
     */
    private void checkNumberOperand(Token operator, Object operand)
    {
        if (operand instanceof Double)
            return;
        
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    /*
     * Checks that the operands are numbers on which the operator
     * can be used.
     */
    private void checkNumberOperands(Token operator, Object left, Object right)
    {
        if (left instanceof Double && right instanceof Double)
            return;
        
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    /*
     * Returns false if the object is false or nil. 
     * Returns true for any other object.
     */
    private boolean isTruthy(Object object)
    {
        if (object == null)
            return false;
        
        if (object instanceof Boolean)
            return (boolean)object;

        return true;
    }

    /*
     * Checks if two objects are equal.
     */
    private boolean isEqual(Object a, Object b)
    {
        if (a == null && b == null)
            return true;
        
        if (a == null)
            return false;

        return a.equals(b);
    }

    /*
     * Returns the string representation of the object.
     */
    private String stringify(Object object)
    {
        if (object == null)
            return "nil";

        if (object instanceof Double)
        {
            String text = object.toString();
            if (text.endsWith(".0"))
                text = text.substring(0, text.length() - 2);

            return text;
        }

        return object.toString();
    }
}
