package gitlet;

import java.util.Scanner;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Jeff Xiang
 */
public class Main {

    /** Version of this Gitlet system. */
    private static final String VERSION = "2.0";

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        System.out.printf("Gitlet Version-Control System %s.%n", VERSION);

        Scanner input = new Scanner(System.in);
        CommandInterpreter interpreter = new CommandInterpreter(input);
        while (true) {
            interpreter.statement();
        }
    }

}
