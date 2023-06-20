package lox_interpreter_java;

/*
 * This class is used for debugging. It creates a string 
 * representation of the syntax tree, so that we make sure 
 * that the operator precedence is handled correctly.
 */
public class AstPrinter implements Expr.Visitor<String>
{
    String print(Expr expr)
    {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) 
    {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) 
    {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) 
    {
        if (expr.value == null)
            return "nil";

        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) 
    {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    /*
     * Returns the string representation of the syntax tree.
     * Calls accept() on each subexpression and passes in itself, 
     * so that the entire tree is printed.
     */
    private String parenthesize(String name, Expr... exprs)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs)
        {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");

        return builder.toString();
    }
}
