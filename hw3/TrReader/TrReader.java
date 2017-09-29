import java.io.Reader;
import java.io.IOException;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Jeff Xiang
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(0) to TO.charAt(0), etc., leaving other characters
     *  unchanged.  FROM and TO must have the same length. */
    public Reader str;
    public String from;
    public String to;
    public TrReader(Reader str, String from, String to) throws IOException {
        this.str = str;
        this.from = from;
        this.to = to;
    }


    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int charsread = str.read(cbuf, off, len);
        for (int i = off; i < cbuf.length; i++) {
            char character = cbuf[i];
            if (from.indexOf(character) >= 0) {
                char change_to = to.charAt(from.indexOf(character));
                cbuf[i] = change_to;
            }
        }
        return charsread;
    }

    @Override
    public void close() throws IOException {
        str.close();
    }

    // FILL IN
    // NOTE: Until you fill in the right methods, the compiler will
    //       reject this file, saying that you must declare TrReader
    //     abstract.  Don't do that; define the right methods instead!
}


