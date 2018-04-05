package types;

public class Implication extends BinaryOperator{
    public Implication(Expression a, Expression b) {
        super(a, b, "->");
    }
}
