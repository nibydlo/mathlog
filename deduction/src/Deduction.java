import types.Expression;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Deduction {


    public static void main(String[] args) throws IOException {
        Deduction d = new Deduction();
        d.run();
    }

    private void run() throws IOException {

        BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), StandardCharsets.UTF_8);
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));

        Checker checker = new Checker();

        String fs = reader.readLine();
        checker.parseHypotheses(fs);

        String allHypos = "";
        String[] hypoArray = fs.split(",");
        for (int i = 0; i < hypoArray.length - 1; i++) {
            allHypos += hypoArray[i];
            if (i != hypoArray.length -2) {
                allHypos += ',';
            }
        }

        writer.write(allHypos + "|-((" + checker.alpha.toNormalString() + ")->(" + checker.beta.toNormalString() + "))\n");

        String line;
        int strNum = 1;

        while((line = reader.readLine()) != null) {

            if (line.equals("")) {
                continue;
            }


            String checkCode = checker.checkStatement(line, strNum);
            createDedProof(checkCode, checker, writer, line);
            strNum++;
        }

        reader.close();
        writer.close();
    }

    private void createDedProof(String checkCode, Checker checker, Writer writer, String sigma) throws IOException {

        ExpressionParser ep = new ExpressionParser();
        sigma = ep.parse(sigma).toNormalString();

        //if sigma is hypotheses
        if (checkCode.charAt(0) == 'H' || checkCode.charAt(0) == 'A') {
            writer.write("(" + sigma + ")" + '\n');
            writer.write("((" + sigma + ")->((" + checker.alpha.toNormalString() + ")->(" + sigma+ ")))\n");
            writer.write("((" + checker.alpha.toNormalString() + ")->(" + sigma + "))\n");
            //writer.write("\n");
            return;
        }

        //if sigma is axiom
        /*if (checkCode.charAt(0) == 'A') {
            writer.write("(" + sigma + ")" + '\n');
            writer.write("(" + sigma + "->(" + checker.alpha.toNormalString() + "->" + sigma+ "))\n");
            return;
        }*/

        //if sigma equals to alpha


        if (checker.alpha.getHash().equals(ep.parse(sigma).getHash())) {
            String a = checker.alpha.toNormalString();

            writer.write("(" + a + "->(" + a + "->" + a + "))\n");
            writer.write("(" + "(" + a + "->("+ a + "->"+ a + "))->" + "(" + a + "->((" + a + "->" + a + ")->" + a + "))->" + "(" + a + "->" + a + "))\n");
            writer.write("(" + "(" + a + "->((" + a + "->" + a + ")->" + a + "))->(" + a + "->" + a + "))\n");
            writer.write("(" + a + "->((" + a + "->" + a + ")->" + a + "))\n");
            writer.write("(" + a + "->" + a + ")\n");
            //writer.write("\n");
            return;
        }

        //if sigma is modus ponens of other sigmas
        if (checkCode.charAt(0) == 'M') {
           // System.out.println("run");
            //getting hash of AB part
            int i = 2;
            String temp = "";
            while (checkCode.charAt(i) != ' ') {
                temp += checkCode.charAt(i);
                i++;
            }
            Integer hashAB = Integer.parseInt(temp);

            i++;
            temp = "";
            while (i < checkCode.length()) {
                temp += checkCode.charAt(i);
                i++;
            }
            Integer hashA = Integer.parseInt(temp);

            String A = checker.alreadyMentionedStr.get(hashA);
            String B = sigma;
            String a = checker.alpha.toNormalString();

            writer.write("(" + a + "->" + A + ")->((" + a + "->(" + A + "->" + B + "))->(" + a + "->" + B + "))\n");

            writer.write("(((" + a + "->(" + A + "->" + B + "))->(" + a + "->" + B + ")))\n");
            writer.write("(" + a + "->" + sigma + ")\n");
            //writer.write("\n");
        }
    }
}