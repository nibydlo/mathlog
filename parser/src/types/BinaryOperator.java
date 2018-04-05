package types;

public class BinaryOperator implements Expression{
    private Expression exp1;
    private Expression exp2;
    private String oper;

    public BinaryOperator(Expression a, Expression b, String c) {
        exp1 = a;
        exp2 = b;
        oper = c;
    }

    @Override
    public String toString() {
        return "(" + oper + ',' + exp1.toString() + "," + exp2.toString() + ")";
    }
}
