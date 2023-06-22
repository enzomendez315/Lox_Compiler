package lox_interpreter_java;

/*
 * This is the base class for all statements.
 * It contains all the subclasses for each statement, and
 * they hold the nonterminals specific to that rule.
 */
public abstract class Stmt 
{
    /*
     * This interface is used to add functionality to the subclasses without 
     * having to implement every method on every subclass.
     */
    public interface Visitor<R>
    {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }

    public static class Expression extends Stmt
    {
        public final Expr expression;

        public Expression(Expr expression)
        {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) 
        {
            return visitor.visitExpressionStmt(this);
        }
    }

    public static class Print extends Stmt
    {
        public final Expr expression;

        public Print(Expr expression)
        {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) 
        {
            return visitor.visitPrintStmt(this);
        }
    }

    public static class Var extends Stmt
    {
        public final Token name;
        public final Expr initializer;

        public Var(Token name, Expr initializer)
        {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) 
        {
            return visitor.visitVarStmt(this);
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
