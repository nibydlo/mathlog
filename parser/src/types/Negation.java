package types;

import java.util.HashMap;
import java.util.Objects;

public class Negation implements Expression{

    private Expression exp;
    private String type;
    private Integer hash = null;

    public Negation(Expression a) {
        exp = a;
        type = "!";
    }

    @Override
    public String toString() {
        return "(" + "!" + exp.toString() + ")";
    }

    @Override
    public String toNormalString() {
        return "!" + exp.toNormalString();
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public Expression getLeftChild() {
        return this.exp;
    }

    @Override
    public Expression getRightChild() {
        return null;
    }

    @Override
    public Integer getHash() {
        if (hash != null) {
            return hash;
        }
        hash = Objects.hash(type, exp.getHash().toString());
        return hash;
    }

    @Override
    public boolean compareStructure(Expression expression, HashMap<String, Integer> map) {
        if (expression.getType() != "!") {
            return false;
        }
        return exp.compareStructure(expression.getLeftChild(), map);
    }
}
