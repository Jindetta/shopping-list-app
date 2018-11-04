package fi.tamk.tiko.ohjelmointi.json;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONTokenizer {

    /**
     *
     */
    private int position;

    /**
     *
     */
    private String input;

    /**
     *
     */
    private char skipToReadableToken() {
        while (position < input.length()) {
            char character = input.charAt(position++);

            switch (character) {
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    continue;

                case '#':
                    // TODO
                    // Single-line comments
                    continue;
                
                case '/':
                    // TODO
                    // Single and multi-line comments
                    continue;

                default:
                    return character;
            }
        }

        throw new JSONException("No readable tokens found.");
    }

    /**
     *
     */
    public JSONTokenizer() {

    }
}