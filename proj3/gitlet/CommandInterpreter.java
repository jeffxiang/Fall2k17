package gitlet;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

/** An object that reads and interperets commands from an input source.
 * @author Jeff Xiang
 * (with inspiration from the CommandInterpreter class in Proj1)
 */

public class CommandInterpreter {

    /** A new CommandInterpreter object. */
    CommandInterpreter(Scanner inp) {
        _input = inp;
    }

    /** Parse and execute one statement from the token stream. */
    void statement() {
        Scanner peek = _input;
        switch (peek.next()) {
            case "init":
                initStatement();
                break;
        }
    }

    /** Parse and execute an init statement. */
    void initStatement() {
        _input.next("init");
        File f = new File(".gitlet");
        boolean success = f.mkdir();
        if (!success) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
        } else {

            System.out.println("Success");

        }
    }

    /** An input scanner from input source. */
    private Scanner _input;

}
