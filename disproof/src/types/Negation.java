package types;

import java.util.ArrayList;
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
    public boolean calculate(ArrayList<ArrayList<Integer>> tree, ArrayList<ArrayList<Integer>> permutation, int world, HashMap<String, Integer> letterVar) {
        return dfsNeg(tree, permutation, world, letterVar);
    }

    public boolean dfsNeg(ArrayList<ArrayList<Integer>> tree, ArrayList<ArrayList<Integer>> permutation, int world, HashMap<String, Integer> letterVar) {

        if (getLeftChild().calculate(tree, permutation, world, letterVar)) {
            return false;
        }

        for (int i = 0; i < tree.get(world).size(); i++) {
            if (getLeftChild().calculate(tree, permutation, tree.get(world).get(i), letterVar)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean compareStructure(Expression expression, HashMap<String, Integer> map) {
        if (expression.getType() != "!") {
            return false;
        }
        return exp.compareStructure(expression.getLeftChild(), map);
    }
}
