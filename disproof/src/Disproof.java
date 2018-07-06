import types.Expression;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Disproof {

    private ArrayList<ArrayList<Integer>> tree = new ArrayList<>(); // tree of Kripke model
    private HashSet<String> vars; // hashset of variables
    private HashMap<String, Integer> letterVar = new HashMap<>(); // map (name of variable -> number of variable)
    private HashMap<Integer, String> varLetter = new HashMap<>();
    private int varCount; // number of variables
    private Expression statement; // expression with statement from input.txt

    public static void main(String[] args) throws IOException {
        Disproof disproof = new Disproof();
        disproof.run();
    }

    private void run() throws IOException {

        // input
        BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), StandardCharsets.UTF_8);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));

        String fs = reader.readLine();

        ExpressionParser expressionParser = new ExpressionParser();
        statement = expressionParser.parse(fs);
        vars = expressionParser.parseVariables(fs);
        int tempNum = 0;
        for (String v : vars) {
            varLetter.put(tempNum, v);
            letterVar.put(v, tempNum++);
        }

        varCount = vars.size();

        // generating parenthesis sequence
        for (int i = 0; i <= 5; i++) {

            boolean lastSequence = false;
            String sequence = "";
            for (int j = 0; j < i; j++) {
                sequence += '(';
            }
            for (int j = 0; j < i; j++) {
                sequence += ')';
            }
            String startSequence = sequence;
            //System.out.println(sequence);
            if (!checkTree(sequence, writer)) {
                //System.out.println("Here if lazha");

                return;
            }
            while (!lastSequence) {


                for (int j = sequence.length() - 1, depth = 0; j >= 0; j--) {
                    if (sequence.charAt(j) == '(') {
                        depth--;
                    } else {
                        depth++;
                    }
                    if (sequence.charAt(j) == '(' && depth > 0) {
                        depth--;
                        int open = (sequence.length() - j - 1 - depth) / 2;
                        int close = sequence.length() - j - 1 - open;
                        sequence = sequence.substring(0, j) + ')';
                        for (int k = 0; k < open; k++) {
                            sequence += '(';
                        }
                        for (int k = 0; k < close; k++) {
                            sequence += ')';
                        }
                        break;
                    }
                }

                lastSequence = true;
                int balance = 0;
                for (int j = 0; j < sequence.length(); j++) {
                    if (sequence.charAt(j) == '(') {
                        balance++;
                    } else {
                        balance--;
                    }
                    if (balance > 1) {
                        lastSequence = false;
                        break;
                    }
                }

                if (!sequence.equals(startSequence)) {
                   // System.out.println(sequence);
                    if (!checkTree(sequence, writer)) {
                        //System.out.println("Here if lazha");
                        reader.close();
                        writer.close();
                        return;
                    }
                }

            } // end of while
        } // end of for

        writer.write("Формула общезначима\n");

        reader.close();
        writer.close();
    }

    private boolean checkTree(String sequence, BufferedWriter writer) throws IOException {

        //tree will be in format of list
        int v = sequence.length() / 2 + 1;
        long permutationCount = (int) Math.pow(2, varCount * v);


        // permutation - table (rows - variables, columns - worlds)
        ArrayList<ArrayList<Integer>> permutation = new ArrayList<>();
        ArrayList<Integer> emptyList = new ArrayList<>();
        for (int i = 0; i < v; i++) {
            emptyList.add(0);
        }
        for (int i = 0; i < varCount; i++) {
            permutation.add(emptyList);
        }

        // getting tree from sequence
        for (int i = 0; i < v; i++) {
            tree.add(new ArrayList<>());
        }

        node = 0;
        fillTree(sequence);
        /*System.out.println("tree:");
        for (int i = 0; i < tree.size(); i++) {
            System.out.println(tree.get(i));
        }*/

        // getting permutation
        for (long i = 0; i < permutationCount; i++) {
            if (checkPermutation(numToPerm(i, v), v)) {
               // System.out.println(numToPerm(i, v));
                permutation = numToPerm(i, v);
                for (int world = 0; world < v; world++) {
                    if (!statement.calculate(tree, permutation, world, letterVar)) {
                        tree.clear();
                        //System.out.println("return false from checkTree");
                        genHeyting(permutation, writer);
                        return false;
                    }
                }
            }
        }

        tree.clear();
        return true;
    }

    // converting Kripke to Heyting
    private void genHeyting(ArrayList<ArrayList<Integer>> permutation, BufferedWriter writer) throws IOException {

        Set<Set<Integer>> Y = new HashSet<>();
        Y.add(new HashSet<Integer>()); // add empty set

        // add first sets for each variable
        for (ArrayList<Integer> a : permutation) {
            Set<Integer> tempSet = new HashSet<>();
            for (int i = 0; i < a.size(); i++) {
                if (a.get(i) == 1) {
                    tempSet.add(i);
                }
            }
            Y.add(tempSet);
        }

        int prevSize = 0;
        int newSize = Y.size();

        while (newSize != prevSize) {
            prevSize = newSize;
            Set<Set<Integer>> ny = new HashSet<>();
            for (Set<Integer> a : Y) {
                for (Set<Integer> b : Y) {
                    if (!a.equals(b)) {
                        Set<Integer> ta = new HashSet<>(a);
                        ta.addAll(b);
                        ny.add(ta);
                        ta = new HashSet<>(a);
                        ta.retainAll(b);
                        ny.add(ta);
                        ny.add(impl(a, b, permutation.get(0).size()));
                    }
                }
            }
            Y.addAll(ny);
            newSize = Y.size();
        }

        //System.out.println("After operations");
        //System.out.println(Y);

        // setting numbers for all sets
        Map<Set<Integer>, Integer> mapY = new HashMap<>();
        Integer num = 0;
        for (Set<Integer> hs : Y) {
            mapY.put(hs, num);
            num++;
        }

        // creating graph: a -> b if a <= b
        ArrayList<ArrayList<Integer>> heytingGraph = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            heytingGraph.add(new ArrayList<>());
        }

        for (Set<Integer> a : Y) {
            for (Set<Integer> b : Y) {
                if (/*!a.equals(b) &&*/ b.containsAll(a)) {
                    heytingGraph.get(mapY.get(a)).add(mapY.get(b));
                }
            }
        }

        writer.write(String.format("%d",heytingGraph.size()));
        writer.newLine();
        for (ArrayList<Integer> al : heytingGraph) {
            for (Integer i : al) {
                writer.write((i + 1) + " ");
            }
            writer.newLine();
        }


        Map<Integer, Integer> varToNode = new HashMap<>();
        // compare new sets with sets from permutation, and, if equal, set this
        for (int i = 0; i < permutation.size(); i++) {
            ArrayList<Integer> a = permutation.get(i);
            Set<Integer> tempSet = new HashSet<>();
            for (int j = 0; j < a.size(); j++) {
                if (a.get(j) == 1) {
                    tempSet.add(j);
                }
            }
            if (mapY.containsKey(tempSet)) {
                varToNode.put(i, mapY.get(tempSet));
            }
        }

        for (int i = 0; i < varCount; i++){
            writer.write(varLetter.get(i) + "=" + (varToNode.get(i) + 1) + "\n");
        }
        writer.flush();
    }

    private Set<Integer> impl(Set<Integer> a, Set<Integer> b, int worldCount) {

        HashSet<Integer> ca = new HashSet<>();

        for (int i = 0; i < worldCount; i++) {
            if (!a.contains(i)) {
                ca.add(i);
            }
        }

        ca.addAll(b);
        return ca;
    }

    private boolean checkPermutation(ArrayList<ArrayList<Integer>> permutation, int v) {

        for (int i = 0; i < varCount; i++) {
            for (int j = 0; j < v; j++) {
                if (permutation.get(i).get(j) == 1) {
                    if (!dfsCheck(permutation, i, j)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean dfsCheck(ArrayList<ArrayList<Integer>> permutation, int var, int vert) {

        if (permutation.get(var).get(vert) != 1) {
            return false;
        }

        for (int i = 0; i < tree.get(vert).size(); i++) {
            if (!dfsCheck(permutation, var, tree.get(vert).get(i))) {
                return false;
            }
        }

        return true;
    }

    private ArrayList<ArrayList<Integer>> numToPerm(long num, int v) {
        ArrayList<ArrayList<Integer>> res = new ArrayList<>();
        for (int i = 0; i < varCount; i++) {
            res.add(new ArrayList<>());
            for (int j = 0; j < v; j++) {
                res.get(i).add((int) num % 2);
                num /= 2;
            }
        }
        return res;
    }

    private int node;

    private void fillTree(String sequence) {

        int start = 0;
        int curNode = node;
        while (start != sequence.length()) {
            int close = start + 1;
            int balance = 1;
            while (balance != 0) {
                if (sequence.charAt(close) == '(') {
                    balance++;
                } else {
                    balance--;
                }
                close++;
            }
            close--;
            tree.get(curNode).add(++node);
            fillTree(sequence.substring(start + 1, close));
            start = close + 1;
        }
    }
}
