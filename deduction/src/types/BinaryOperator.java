package types;

import java.util.HashMap;
import java.util.Objects;

public class BinaryOperator implements Expression{

    private Expression exp1;
    private Expression exp2;
    private String type;
    private Integer hash = null;

    public BinaryOperator(Expression a, Expression b, String c) {
        exp1 = a;
        exp2 = b;
        type = c;
    }

    @Override
    public String toString() {
        return "(" + type + ',' + exp1.toString() + "," + exp2.toString() + ")";
    }

    public String toNormalString() {
        return "(" + exp1.toNormalString() + type + exp2.toNormalString() + ")";
    }

    @Override
    public Expression getLeftChild() {
        return exp1;
    }

    @Override
    public Expression getRightChild() {
        return exp2;
    }

    @Override
    public boolean compareStructure(Expression expression, HashMap<String, Integer> map) {

        if (!expression.getType().equals(this.getType())) {
            return false;
        } else {
            return exp1.compareStructure(expression.getLeftChild(), map) && exp2.compareStructure(expression.getRightChild(), map);
        }
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Integer getHash() {
        if (hash != null) {
            return hash;
        }
        hash = Objects.hash(type, exp1.getHash().toString(), exp2.getHash().toString());
        return hash;
    }
}
