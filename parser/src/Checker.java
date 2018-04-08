import types.Expression;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Checker {

    private ArrayList<Expression> axioms = new ArrayList<>();
    private HashMap<Integer, Integer> hypoMap = new HashMap<>(); //key - hask of hypo, value - number
    private HashMap<Integer, Integer> alreadyMentioned = new HashMap<>(); //key - hash of proved expr, value - number of str, where it was proved
    private HashMap<Integer, Map.Entry<Integer, Integer>> mpMapProved = new HashMap<>(); //key - hash of B, key - hash of A->B, value - hash of A

    public static void main(String[] args) throws IOException {
        Checker checker = new Checker();
        checker.run();
    }

    private void run() throws IOException {
        BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), StandardCharsets.UTF_8);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));

        String fs = reader.readLine();
        while (fs.equals("")) {
            fs = reader.readLine();
        }

        Checker checker = new Checker();
        checker.parseHypotheses(fs);

        //creating axioms
        ExpressionParser ep = new ExpressionParser();
        checker.axioms.add(ep.parse("A->(B->A)"));
        checker.axioms.add(ep.parse("(A->B)->(A->B->C)->(A->C)"));
        checker.axioms.add(ep.parse("A->B->A&B"));
        checker.axioms.add(ep.parse("A&B->A"));
        checker.axioms.add(ep.parse("A&B->B"));
        checker.axioms.add(ep.parse("A->A|B"));
        checker.axioms.add(ep.parse("A->B|A"));
        checker.axioms.add(ep.parse("(A->B)->(C->B)->(A|C->B)"));
        checker.axioms.add(ep.parse("(A->B)->(A->!B)->!A"));
        checker.axioms.add(ep.parse("!!A->A"));

        String line;
        int strNum = 1;
        while ((line = reader.readLine()) != null) {

            if (line.equals("")) {
                continue;
            }
            if (strNum != 1) {
                writer.write("\n");
            }
            checker.checkStatement(line, strNum, writer);
            strNum++;
        }

        reader.close();
        writer.close();
    }

    private void parseHypotheses(String s) {

        int i = 0;
        ExpressionParser ep = new ExpressionParser();
        while (s.charAt(i) != '-') {
            String hypothesis = "";
            while (s.charAt(i) != ',' && s.charAt(i) != '|') {
                hypothesis += s.charAt(i);
                i++;
            }
            if (hypothesis != "") {
                hypoMap.put(ep.parse(hypothesis).getHash(), hypoMap.size());
            }
            i++;
        }
    }

    private void checkStatement(String str, Integer strNum, BufferedWriter writer) throws IOException {

        ExpressionParser ep = new ExpressionParser();
        Expression expression = ep.parse(str);

        //adding information for modus ponens;
        if (expression.getType().equals("->")) {
            Map.Entry<Integer, Integer> tempEntry = new AbstractMap.SimpleEntry<>(expression.getHash(), expression.getLeftChild().getHash());
            mpMapProved.put(expression.getRightChild().getHash(), tempEntry);
        }

        //cheching hypos
        Integer hypoPos = hypoMap.get(expression.getHash());
        if (hypoPos != null) {
            writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (Предп. " + (hypoPos + 1) + ")");
            alreadyMentioned.put(expression.getHash(), strNum);
            return;
        }


        //checking for axioms
        for (int i = 0; i < 10; i++) {
            HashMap<String, Integer> varMap = new HashMap<>(); //string - name of variable in axiom, Integer - hash of appropriate expression
            if (axioms.get(i).compareStructure(expression, varMap)) {
                alreadyMentioned.put(expression.getHash(), strNum);
                writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (Сх. акс. " + (i + 1) + ")");
                return;
            }
        }

        //checking for modus ponens
        if (mpMapProved.get(expression.getHash()) != null && alreadyMentioned.get(mpMapProved.get(expression.getHash()).getValue()) != null) {
                alreadyMentioned.put(expression.getHash(), strNum);
                Integer numAB = alreadyMentioned.get(mpMapProved.get(expression.getHash()).getKey());
                Integer numA = alreadyMentioned.get(mpMapProved.get(expression.getHash()).getValue());
                writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (M.P. " + numAB + ", " + numA + ")");
                return;
        }

        //default
        alreadyMentioned.put(expression.getHash(), strNum);
        writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (Не доказано)");
    }
}
