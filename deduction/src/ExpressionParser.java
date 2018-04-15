import types.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ExpressionParser {

    private static String expression;
    private int i;

    private enum Type {VAR, CON, DIS, NEG, IMPL, LBRACKET, RBRACKET}

    private Type curType;
    private String var;

    private char nextChar() {
        if (i < expression.length()) {
            char c = expression.charAt(i);
            i++;
            return c;
        } else {
            return '#';
        }
    }

    private void getSmth() {
        //System.out.println(i);
        char c = nextChar();
        //System.out.println("char = " + c);
        //System.out.println(i);
        switch (c) {
            case '-':
                //System.out.println("run -");
                i++;
                curType = Type.IMPL;
                break;
            case '&':
                //System.out.println("run +");
                curType = Type.CON;
                break;
            case '|':
                //System.out.println("run |");
                curType = Type.DIS;
                break;
            case '!':
                //System.out.println("run !");
                curType = Type.NEG;
                break;
            case '(':
                //System.out.println("run (");
                curType = Type.LBRACKET;
                break;
            case ')':
                //System.out.println("run )");
                curType = Type.RBRACKET;
                break;
            default:
                curType = Type.VAR;
                String newVar = "";
                newVar += c;
                while (i < expression.length() && (Character.isLetter(expression.charAt(i)) || Character.isDigit(expression.charAt(i)))) {
                    newVar += expression.charAt(i);
                    i++;
                }

                var = newVar;
                //System.out.println("var == " + var);
        }
    }

    private Expression highPriority() {
        //System.out.println("run high priority");
        getSmth();
        Expression t;
        switch (curType) {
            case VAR:
                t = new Variable(var);
                getSmth();
                break;
            case NEG:
                t = new Negation(highPriority());
                break;
            case LBRACKET:
                t = impl();
                getSmth();
                break;
            default:
                t = null;
                //System.out.println("argument not found");
        }
        return t;
    }

    private Expression con() {
        //System.out.println("run con");
        Expression left = highPriority();
        while(true) {
            switch (curType) {
                case CON:
                    left = new Conjunction(left, highPriority());
				break;
                default:
                    return left;
            }
        }
    }

    private Expression dis() {
        //System.out.println("run dis");
        Expression left = con();
        while (true) {
            switch (curType) {
                case DIS:
                    left = new Disjunction(left, con());
                    break;
                default:
                    return left;
            }
        }
    }

    private Expression impl() {
        //System.out.println("run impl");
        Expression left = dis();
        while (true) {
            switch (curType) {
                case IMPL:
                    left = new Implication(left, impl());
                    break;
                default:
                    return left;
            }
        }
    }

    public static void main(String[] args) throws IOException {

        BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), StandardCharsets.UTF_8);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));
        String arg = "";
        String line;
        while ((line = reader.readLine()) != null){
            arg += line;
        }
        //System.out.println("arg is " + arg);
        ExpressionParser ep = new ExpressionParser();
        //System.out.println(ep.run(arg).toString());
        writer.write(ep.parse(arg).toString());
        writer.close();
        reader.close();

    }

    public Expression parse(String str) {
        expression = "";
        for (int it = 0; it < str.length(); it++) {
            if (!Character.isWhitespace(str.charAt(it)) && str.charAt(it) != '\t' && str.charAt(it) != '\n') {
                expression += str.charAt(it);
            }
        }
        //System.out.println(expression);
        i = 0;

        return impl();
    }
}
