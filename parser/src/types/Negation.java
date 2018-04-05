package types;

public class Negation implements Expression{

    private Expression exp;

    public Negation(Expression a) {
        exp = a;
    }

    @Override
    public String toString() {
        return "(" + "!" + exp.toString() + ")";
    }
}
