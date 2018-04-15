package types;

import java.util.HashMap;

public interface Expression {

    String getType();

    String toString();

    String toNormalString();

    public boolean compareStructure(Expression expression, HashMap<String, Integer> map);

    public Expression getLeftChild();

    public Expression getRightChild();

    public Integer getHash();
}
