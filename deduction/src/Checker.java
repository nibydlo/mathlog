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

    public ArrayList<Expression> axioms = new ArrayList<>();
    public HashMap<Integer, Integer> hypoMap = new HashMap<>(); //key - hask of hypo, value - number
    public HashMap<Integer, Integer> alreadyMentioned = new HashMap<>(); //key - hash of proved expr, value - number of str, where it was proved
    public HashMap<Integer, String> alreadyMentionedStr = new HashMap<>(); //key - hash of proved expr, valye - string implemntation of str
    public HashMap<Integer, ArrayList<Map.Entry<Integer, Integer>>> mpMapProved = new HashMap<>(); //key - hash of B, key - hash of A->B, value - hash of A

    public Expression alpha = null; //alpha
    public Expression beta; //beta
    public Integer commaCount = 0;

    public Checker() {
        ExpressionParser ep = new ExpressionParser();
        axioms.add(ep.parse("A->(B->A)"));
        axioms.add(ep.parse("(A->B)->(A->B->C)->(A->C)"));
        axioms.add(ep.parse("A->B->A&B"));
        axioms.add(ep.parse("A&B->A"));
        axioms.add(ep.parse("A&B->B"));
        axioms.add(ep.parse("A->A|B"));
        axioms.add(ep.parse("A->B|A"));
        axioms.add(ep.parse("(A->B)->(C->B)->(A|C->B)"));
        axioms.add(ep.parse("(A->B)->(A->!B)->!A"));
        axioms.add(ep.parse("!!A->A"));
    }

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
        /*checker.axioms.add(ep.parse("A->(B->A)"));
        checker.axioms.add(ep.parse("(A->B)->(A->B->C)->(A->C)"));
        checker.axioms.add(ep.parse("A->B->A&B"));
        checker.axioms.add(ep.parse("A&B->A"));
        checker.axioms.add(ep.parse("A&B->B"));
        checker.axioms.add(ep.parse("A->A|B"));
        checker.axioms.add(ep.parse("A->B|A"));
        checker.axioms.add(ep.parse("(A->B)->(C->B)->(A|C->B)"));
        checker.axioms.add(ep.parse("(A->B)->(A->!B)->!A"));
        checker.axioms.add(ep.parse("!!A->A"));*/

        String line;
        int strNum = 1;
        while ((line = reader.readLine()) != null) {

            if (line.equals("")) {
                continue;
            }
            if (strNum != 1) {
                writer.write("\n");
            }
            checker.checkStatement(line, strNum);
            strNum++;
        }

        reader.close();
        writer.close();
    }

    public void parseHypotheses(String s) {

        ExpressionParser ep = new ExpressionParser();
        String[] hypoArray = s.split(",");
        for (int i = 0; i < hypoArray.length - 1; i++) {
            hypoMap.put(ep.parse(hypoArray[i]).getHash(), i + 1);
        }

        int i = 0;
        s = hypoArray[hypoArray.length - 1];
        String alphaStr = "";

        while (s.charAt(i) != '|') {
            alphaStr += s.charAt(i);
            i++;
        }
        alpha = ep.parse(alphaStr);
        i += 2;
        String stString = "";
        while (i < s.length()) {
            stString += s.charAt(i);
            i++;
        }
        beta = ep.parse(stString);
    }

    public String checkStatement(String str, Integer strNum) throws IOException {

        ExpressionParser ep = new ExpressionParser();
        Expression expression = ep.parse(str);

        //adding information for modus ponens;
        if (expression.getType().equals("->")) {
            if (mpMapProved.get(expression.getRightChild().getHash()) == null) {
                ArrayList<Map.Entry<Integer, Integer>> tempList = new ArrayList<>();
                Map.Entry<Integer, Integer> tempEntry = new AbstractMap.SimpleEntry<>(expression.getHash(), expression.getLeftChild().getHash());
                tempList.add(tempEntry);
                mpMapProved.put(expression.getRightChild().getHash(), tempList);
            } else {
                Map.Entry<Integer, Integer> tempEntry = new AbstractMap.SimpleEntry<>(expression.getHash(), expression.getLeftChild().getHash());
                mpMapProved.get(expression.getRightChild().getHash()).add(tempEntry);
            }
        }

        //cheching hypos
        Integer hypoPos = hypoMap.get(expression.getHash());
        if (hypoPos != null) {
            //writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (Предп. " + (hypoPos + 1) + ")");
            alreadyMentionedStr.put(expression.getHash(), expression.toNormalString());
            alreadyMentioned.put(expression.getHash(), strNum);
            return "H " + (hypoPos + 1);
        }


        //checking for axioms
        for (int i = 0; i < 10; i++) {
            HashMap<String, Integer> varMap = new HashMap<>(); //string - name of variable in axiom, Integer - hash of appropriate expression
            if (axioms.get(i).compareStructure(expression, varMap)) {
                alreadyMentionedStr.put(expression.getHash(), expression.toNormalString());
                alreadyMentioned.put(expression.getHash(), strNum);
                //writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (Сх. акс. " + (i + 1) + ")");
                return "A " + (i+1);
            }
        }

        //checking for modus ponens
        if (mpMapProved.get(expression.getHash()) != null) {
            for (int i = 0; i < mpMapProved.get(expression.getHash()).size(); i++) {
                if (alreadyMentioned.get(mpMapProved.get(expression.getHash()).get(i).getValue()) != null) {
                    alreadyMentioned.put(expression.getHash(), strNum);
                    alreadyMentionedStr.put(expression.getHash(), expression.toNormalString());
                    Integer numAB = alreadyMentioned.get(mpMapProved.get(expression.getHash()).get(i).getKey());
                    Integer numA = alreadyMentioned.get(mpMapProved.get(expression.getHash()).get(i).getValue());
                    //writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (M.P. " + numAB + ", " + numA + ")");
                    return "M " + mpMapProved.get(expression.getHash()).get(i).getKey() + " " + mpMapProved.get(expression.getHash()).get(i).getValue();
                }
            }
        }

        //default
        alreadyMentioned.put(expression.getHash(), strNum);
        alreadyMentionedStr.put(expression.getHash(), expression.toNormalString());
        //writer.write("(" + strNum + ")" + " " + expression.toNormalString() + " (Не доказано)");
        return "N";
    }
}
