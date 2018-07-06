package types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Variable implements Expression {

    private String name;
    private String type;
    private Integer hash = null;

    public Variable(String a) {
        name = a;
        type = "var";
    }

    @Override
    public String getType() {
        return type;
    }

    public String toString() {
        return name;
    }

    public String toNormalString() {
        return name;
    }

    @Override
    public boolean compareStructure(Expression expression, HashMap<String, Integer> map) {
        //if (expression.getType() == "var") {
            if (map.get(name) == null) {
                map.put(name, expression.getHash());
                return true;
            } else if (map.get(name).equals(expression.getHash())) {
                return true;
            }

        return false;
    }

    @Override
    public Expression getLeftChild() {
        return null;
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
        hash = Objects.hash(type, name);
        return hash;
    }

    @Override
    public boolean calculate(ArrayList<ArrayList<Integer>> tree, ArrayList<ArrayList<Integer>> permutation, int world, HashMap<String, Integer> letterVar) {
        return  permutation.get(letterVar.get(name)).get(world) == 1;
    }
}
