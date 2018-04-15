package types;

public class Disjunction extends BinaryOperator{
    public Disjunction(Expression a, Expression b) {
        super(a, b, "|");
    }
}
