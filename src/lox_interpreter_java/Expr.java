package lox_interpreter_java;

/*
 * This is the base class for all expressions.
 * It contains all the subclasses for each expression, and
 * they hold the nonterminals specific to that rule.
 */
public abstract class Expr 
{
    public static class Binary extends Expr
    {
        public final Expr left;
        public final Token operator;
        public final Expr right;

        Binary(Expr left, Token operator, Expr right)
        {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }
    }

    public static class Grouping extends Expr
    {
        public final Expr expression;

        Grouping(Expr expression)
        {
            this.expression = expression;
        }
    }

    public static class Literal extends Expr
    {
        public final Object value;

        Literal(Object value)
        {
            this.value = value;
        }
    }

    public static class Unary extends Expr
    {
        public final Token operator;
        public final Expr right;

        Unary(Token operator, Expr right)
        {
            this.operator = operator;
            this.right = right;
        }
    }
}
