import types.Expression;

import java.util.ArrayList;

public class ProvedExpressions {

    private ArrayList<Expression> provedExpression = new ArrayList<>();

    public void add(Expression expression) {
        provedExpression.add(expression);
    }

    public Expression get(int i) {
        return provedExpression.get(i);
    }

    public int size() {
        return provedExpression.size();
    }
}
