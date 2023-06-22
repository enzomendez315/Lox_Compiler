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

    public abstract <R> R accept(Visitor<R> visitor);
}
