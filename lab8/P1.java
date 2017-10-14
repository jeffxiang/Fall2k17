import java.util.*;
import java.io.*;

public class P1 {

    public static void main(String... ignored) {
        Scanner input = new Scanner(System.in);
        List<String> lines = new ArrayList<>();
        List<Integer> listspaces = new ArrayList<>();
        int image = 0;
        while (input.hasNextLine()) {
            String line = input.nextLine();
            int countspacesremaining = 0;
            while (line.length() != 0) {
                for (String oneline : lines) {
                    int spacesinline = 0;
                    for (int i = 0; i < oneline.length(); i++) {
                        Character charval = oneline.charAt(i);
                        String val = charval.toString();
                        if (!val.equals("X")) {
                            spacesinline++;
                        }
                    }
                    listspaces.add(spacesinline);
                }
                int minspaces = listspaces.get(0);
                for (int spacesinline : listspaces) {
                    if (spacesinline < minspaces) {
                        minspaces = spacesinline;
                    }
                }
                for (int spacesinline : listspaces) {
                    spacesinline -= minspaces;
                    countspacesremaining += spacesinline;
                }
                line = input.nextLine();
            }
            System.out.print("Image " + image + ": " + countspacesremaining);
        }
    }
}