package types;

public class Variable implements Expression {

    private String name;

    public Variable(String a) {
        name = a;
    }

    public String toString() {
        return name;
    }
}
