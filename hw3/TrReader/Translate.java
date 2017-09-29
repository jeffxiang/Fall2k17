import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

/** String translation.
 *  @author Jeff Xiang
 */
public class Translate {
    /** Return the String S, but with all characters that occur in FROM
     *  changed to the corresponding characters in TO. FROM and TO must
     *  have the same length. */
    static String translate(String S, String from, String to) throws IOException {
        /* NOTE: The try {...} catch is a technicality to keep Java happy. */
        char[] buffer = new char[S.length()];
        Reader r = new StringReader(S);
        TrReader trR = new TrReader(r, from, to);
        try {
            trR.read(buffer, 0, buffer.length);
        } catch (IOException e) {
            return null;
        }
        return new String(buffer);
    }
    /*
       REMINDER: translate must
      a. Be non-recursive
      b. Contain only 'new' operations, and ONE other method call, and no
         other kinds of statement (other than return).
      c. Use only the library classes String, and anything containing
         "Reader" in its name (browse the on-line documentation).
    */
}
