import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class Grid {

    BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), StandardCharsets.UTF_8);
    BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));

    ArrayList<ArrayList<Integer>> g = new ArrayList<>(), gRev = new ArrayList<>(), sum = new ArrayList<>(), mult = new ArrayList<>(),  impl = new ArrayList<>(), rel = new ArrayList<>();
    ArrayList<Boolean> used = new ArrayList<>();
    Integer n = Integer.parseInt(reader.readLine());

    public Grid() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Grid grid = new Grid();
        grid.run();
    }

    private void run() throws IOException {

        //input
        for (int i = 0; i < n; i++) {
            g.add(new ArrayList<>());
            gRev.add(new ArrayList<>());
            sum.add(new ArrayList<>());
            mult.add(new ArrayList<>());
            impl.add(new ArrayList<>());
            rel.add(new ArrayList<>());
            for (int j = 0; j < n; j++) {
                sum.get(i).add(-1);
                mult.get(i).add(-1);
                impl.get(i).add(-1);
                rel.get(i).add(-1);
            }
            used.add(false);
        }

        for (int i = 0; i < n; i++) {
            int finalI = i;
            Arrays.stream(reader.readLine().split(" ")).forEach((e) -> {
                Integer temp = Integer.parseInt(e) - 1;
                g.get(finalI).add(temp);
                gRev.get(temp).add(finalI);
            });
        }

        //add reflex
        for (int i = 0; i < g.size(); i++) {
            if (g.get(i).indexOf(i) == -1) {
                g.get(i).add(i);
            }
            if (gRev.get(i).indexOf(i) == -1) {
                gRev.get(i).add(i);
            }
        }

        //checking input
        /*for (int i = 0; i < g.size(); i++) {
            for (int j = 0; j < g.get(i).size(); j++) {
                System.out.print(g.get(i).get(j) + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < gRev.size(); i++) {
            for (int j = 0; j < gRev.get(i).size(); j++) {
                System.out.print(gRev.get(i).get(j) + " ");
            }
            System.out.println();
        }*/

        //calculate relations
        for (int i = 0; i < n; i++) {
            clear(used);
            dfs(i, g);
            for (int j = 0; j < n; j++) {
                if (used.get(j)) {
                    rel.get(j).set(i, 1);
                    rel.get(i).set(j, 0);
                }
            }
            rel.get(i).set(i, 2);

        }

        //check rel
       /* for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(rel.get(i).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();*/

        //calculate sum
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ArrayList<Integer> ar = new ArrayList<>();
                int upperBound = -1;
                for (int k = 0; k < n; k++) {
                    if (rel.get(k).get(i) >= 1 && rel.get(k).get(j) >= 1) { // k >= i && k >= j
                        if (upperBound == -1 || rel.get(k).get(upperBound) == 0) { // not set || k < upperBound
                            upperBound = k;
                        } else if (rel.get(k).get(upperBound) == -1){
                            ar.add(k);
                        }
                    }
                }

                if (upperBound == -1) { // not set
                    writer.write("Операция '+' не определена: " + (i + 1) + "+" + (j + 1));
                    reader.close();
                    writer.close();
                    return;
                }

                for (int it = 0; it < ar.size(); it++) {
                    if (rel.get(upperBound).get(ar.get(it)) == -1) { // upper > ar[i] || rel not set
                        writer.write("Операция '+' не определена: " + (i + 1) + "+" + (j + 1));
                        reader.close();
                        writer.close();
                        return;
                    }
                }

                sum.get(i).set(j, upperBound);
                sum.get(j).set(i, upperBound);
            }
        }

        //checking sum
        /*for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(sum.get(i).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();*/

        //calculate mult
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {

                ArrayList<Integer> ar = new ArrayList<>();
                int lowerBound = -1;
                for (int k = 0; k < n; k++) {
                    if (rel.get(k).get(i) % 2 == 0 && rel.get(k).get(j) % 2 == 0) { // k <= i && k <= j
                        if (lowerBound == -1 || rel.get(k).get(lowerBound) == 1) { //not set || k > lowerBound
                            lowerBound = k;
                        } else if (rel.get(k).get(lowerBound) == -1){
                            ar.add(k);
                        }
                    }
                }

                if (lowerBound == -1) { // not set
                    writer.write("Операция '*' не определена: " + (i + 1) + "+" + (j + 1));
                    reader.close();
                    writer.close();
                    return;
                }

                for (int it = 0; it < ar.size(); it++) {
                    if (rel.get(lowerBound).get(ar.get(it)) == -1) { //lower < ar[it] || rel not set
                        writer.write("Операция '*' не определена: " + (i + 1) + "+" + (j + 1));
                        reader.close();
                        writer.close();
                        return;
                    }
                }

                mult.get(i).set(j, lowerBound);
                mult.get(j).set(i, lowerBound);
            }
        }

        //checking mult
     /*  for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(mult.get(i).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();*/

        //check distr
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    Integer left = mult.get(i).get(sum.get(j).get(k));
                    Integer right = sum.get(mult.get(i).get(j)).get(mult.get(i).get(k));
                    if (left != right) {
                        writer.write("Нарушается дистрибутивность: " + (i + 1) + "*(" + (j + 1) + "+" + (k + 1) + ")");
                        reader.close();
                        writer.close();
                        return;
                    }
                }
            }
        }

        //calculate impl
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                ArrayList<Integer> ar = new ArrayList<>();
                int implBound = -1;
                for (int k = 0; k < n; k++) {
                    if (rel.get(mult.get(i).get(k)).get(j) % 2 == 0) { // i * k <= j
                        if (implBound == -1 || rel.get(k).get(implBound) >= 1) { // not set || k > implBound
                            implBound = k;
                        } else if (rel.get(k).get(implBound) == -1){ //rel not set
                            ar.add(k);
                        }
                    }
                }

                if (implBound == -1) { // not set
                    writer.write("Операция '->' не определена: " + (i + 1) + "+" + (j + 1));
                    reader.close();
                    writer.close();
                    return;
                }

                for (int it = 0; it < ar.size(); it++) {
                    if (rel.get(implBound).get(ar.get(it)) == -1) { // impl < ar[it] || rel not set
                        writer.write("Операция '->' не определена: " + (i + 1) + "+" + (j + 1));
                        reader.close();
                        writer.close();
                        return;
                    }
                }

                impl.get(i).set(j, implBound);
            }
        }

        //checking impl
      /* for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(impl.get(i).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println();*/

        //find 0
        int zero = -1;
        for (int i = 0; i < n; i++) {
            int flag = 0;
            for (int j = 0; j < n; j++) {
                if (rel.get(i).get(j) == -1 || rel.get(i).get(j) == 1) { // rel not set || i > j
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                zero = i;
                break;
            }
        }

        //find 1
        int one = -1;
        for (int i = 0; i < n; i++) {
            int flag = 0;
            for (int j = 0; j < n; j++) {
                if (rel.get(i).get(j) == -1 || rel.get(i).get(j) == 0) { // rel not set || i < j
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                one = i;
                break;
            }
        }

     //   System.out.println("0 is " + zero + " 1 is " + one);

        //check a + ~a
        for (int i = 0; i < n; i++) {
            if (sum.get(i).get(impl.get(i).get(zero)) != one) {
                writer.write("Не булева алгебра: " + (i + 1) + "+~" + (i + 1));
                reader.close();
                writer.close();
                return;
            }
        }

        //on success
        writer.write("Булева алгебра");
        reader.close();
        writer.close();
    }

    private void clear(ArrayList<Boolean> a) {
        for (int i = 0; i < a.size(); i++) {
            a.set(i, false);
        }
    }

    private void dfs(int i, ArrayList<ArrayList<Integer>> g) {
        if (!used.get(i)) {
            used.set(i, true);
            for (int j = 0; j < g.get(i).size(); j++) {
                dfs(g.get(i).get(j), g);
            }
        }
    }
}
