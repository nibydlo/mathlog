public class Utils {

    public static String cleanStr(String str) {
        String ans = "";
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i)) && str.charAt(i) != '\t' && str.charAt(i) != 13) {
                ans += str.charAt(i);
            }
        }
        return ans;
    }
}
