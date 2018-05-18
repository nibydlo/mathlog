import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class Grid2 {

    BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), StandardCharsets.UTF_8);
    BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));

    ArrayList<ArrayList<Integer>> g = new ArrayList<>(), rel = new ArrayList<>(), sum = new ArrayList<>(), mult = new ArrayList<>(), impl = new ArrayList<>();
    ArrayList<Boolean> used = new ArrayList<>();

    Integer n = Integer.parseInt(reader.readLine());

    public Grid2() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Grid2 grid2 = new Grid2();
        grid2.run();
    }

    public void run() throws IOException {

        //input
        for (int i = 0; i < n; i++) {
            g.add(new ArrayList<>());
            rel.add(new ArrayList<>());
            sum.add(new ArrayList<>());
            mult.add(new ArrayList<>());
            impl.add(new ArrayList<>());
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
                    }
            );
        }

        //calculate relations
        for (int i = 0; i < n; i++) {
            clear();
            dfs(i);
            for (int j = 0; j < n; j++) {
                if (used.get(j)) {
                    rel.get(j).set(i, 1);
                    rel.get(i).set(j, 0);
                }
            }
            rel.get(i).set(i, 2);
        }

        //calculate sum
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                ArrayList<Integer> suspect = new ArrayList<>();
                int upperBound = -1;
                for (int k = 0; k < n; k++) {
                    if (rel.get(k).get(i) >= 1 && rel.get(k).get(j) >=1) {
                        if (upperBound == -1 || rel.get(k).get(upperBound) == 0) {
                            upperBound = k;
                        } else if (rel.get(k).get(upperBound) == -1) {
                            suspect.add(k);
                        }
                    }
                }

                if (upperBound == -1) {
                    writer.write("Операция '+' не определена: " + (i + 1) + "+" + (j + 1));
                    reader.close();
                    writer.close();
                    return;
                }

                for (int k = 0; k < suspect.size(); k++) {
                    if (rel.get(upperBound).get(suspect.get(k)) == -1) {
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

        //calculate mult
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                ArrayList<Integer> suspect = new ArrayList<>();
                int lowerBound = -1;
                for (int k = 0; k < n; k++) {
                    if (rel.get(k).get(i) % 2 == 0 && rel.get(k).get(j) % 2 == 0) {
                        if (lowerBound == -1 || rel.get(k).get(lowerBound) == 1) {
                            lowerBound = k;
                        } else if (rel.get(k).get(lowerBound) == -1) {
                            suspect.add(k);
                        }
                    }
                }

                if (lowerBound == -1) {
                    writer.write("Операция '*' не определена: " + (i + 1) + "*" + (j + 1));
                    reader.close();
                    writer.close();
                    return;
                }

                for (int k = 0; k < suspect.size(); k++) {
                    if (rel.get(lowerBound).get(suspect.get(k)) == -1) {
                        writer.write("Операция '*' не определена: " + (i + 1) + "*" + (j + 1));
                        reader.close();
                        writer.close();
                        return;
                    }
                }

                mult.get(i).set(j, lowerBound);
                mult.get(j).set(i, lowerBound);
            }
        }

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

                ArrayList<Integer> suspect = new ArrayList<>();
                int implBound = -1;
                for (int k = 0; k < n; k++) {
                    if (rel.get(mult.get(i).get(k)).get(j) % 2 == 0) {
                        if (implBound == -1 || rel.get(k).get(implBound) >= 1) {
                            implBound = k;
                        } else if (rel.get(k).get(implBound) == -1) {
                            suspect.add(k);
                        }
                    }
                }

                if (implBound == -1) {
                    writer.write("Операция '->' не определена: " + (i + 1) + "*" + (j + 1));
                    reader.close();
                    writer.close();
                    return;
                }

                for (int k = 0; k < suspect.size(); k++) {
                    if (rel.get(implBound).get(suspect.get(k)) == -1) {
                        writer.write("Операция '->' не определена: " + (i + 1) + "*" + (j + 1));
                        reader.close();
                        writer.close();
                        return;
                    }
                }

                impl.get(i).set(j, implBound);
            }
        }

        //check boolalg
        Integer zero = -1;
        for (int i = 0; i < n; i++) {
            Boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (rel.get(i).get(j) == -1 || rel.get(i).get(j) == 1) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                zero = i;
                break;
            }
        }


        Integer one = -1;
        for (int i = 0; i < n; i++) {
            Boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (rel.get(i).get(j) == -1 || rel.get(i).get(j) == 0) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                one = i;
                break;
            }
        }

        for (int i = 0; i < n; i++) {
            if (sum.get(i).get(impl.get(i).get(zero)) != one) {
                writer.write("Не булева алгебра: " + (i + 1) + "+~" + (i + 1));
                reader.close();
                writer.close();
                return;
            }
        }

        writer.write("Булева алгебра");
        reader.close();
        writer.close();
    }

    private void clear() {
        for (int i = 0; i < used.size(); i++) {
            used.set(i, false);
        }
    }

    private void dfs(int v) {
        if (used.get(v)) {
            return;
        }
        used.set(v, true);
        for (int i = 0; i < g.get(v).size(); i++) {
            dfs(g.get(v).get(i));
        }
    }
}
