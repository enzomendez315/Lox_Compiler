package lox_interpreter_java;

/*
 * This class is used to evaluate expressions and produce values 
 * using the syntax trees created by the parser.
 */
public class Interpreter implements Expr.Visitor<Object>
{

    private Object evaluate(Expr expr)
    {
        return expr.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) 
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBinaryExpr'");
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) 
    {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) 
    {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) 
    {
        Object right = evaluate(expr.right);

        switch (expr.operator.type)
        {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                return -(double)right;
        }

        return null;
    }
}
