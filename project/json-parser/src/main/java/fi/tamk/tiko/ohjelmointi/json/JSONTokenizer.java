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
     *
     * @param findEOL
     * @return
     */
    private int skipVoidTokens(boolean findEOL) {
        while (position < input.length()) {
            int character = input.charAt(position++);

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

        return -1;
    }

    /**
     *
     *
     * @return
     */
    public Object parseNext() {
        int token = skipVoidTokens(false);

        if (token != -1) {
            switch (token) {
                case '{':
                    return null; // TODO: Create JSONObject

                case '[':
                    return null; // TODO: Create JSONArray

                case '"':
                case '\'':
                    return null; // TODO: Create String
            }

            return null; // TODO: Create literal (boolean, number, decimal, null)
        }

        throw new JSONException("End of file encountered while parsing.");
    }

    /**
     *
     */
    public JSONTokenizer() {

    }
}