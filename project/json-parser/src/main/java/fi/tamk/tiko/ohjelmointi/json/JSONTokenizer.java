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
    private char skipVoidTokens(boolean findEOL) {
        while (position < input.length()) {
            char character = input.charAt(position++);

            if (!findEOL) {
                switch (character) {
                    case ' ':
                    case '\t':
                    case '\r':
                    case '\n':
                        continue;

                    case '#':
                        skipVoidTokens(true);
                        continue;

                    case '/':
                        if (position < input.length()) {
                            switch (input.charAt(position)) {
                                case '*':
                                    int endIndex = input.indexOf("*/", ++position);

                                    if (endIndex == -1) {
                                        throw new JSONException("Unterminated comment.");
                                    }

                                    position = endIndex + 2;
                                    continue;

                                case '/':
                                    skipVoidTokens(true);
                                    continue;
                            }
                        }

                    default:
                        return character;
                }
            } else if (character == '\r' || character == '\n') {
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