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
    private char skipToReadableToken(boolean findEOL) {
        while (position < input.length()) {
            char character = input.charAt(position++);

            switch (character) {
                case '\r':
                case '\n':
                    if (findEOL) {
                        return character;
                    }

                    continue;

                case ' ':
                case '\t':
                    continue;

                case '#':
                    if (!findEOL) {
                        skipToReadableToken(true);
                    }

                    continue;

                case '/':
                    if (!findEOL) {
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
                                    skipToReadableToken(true);
                                    continue;
                            }
                        }

                        return character;
                    }

                    continue;

                default:
                    if (!findEOL) {
                        return character;
                    }
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