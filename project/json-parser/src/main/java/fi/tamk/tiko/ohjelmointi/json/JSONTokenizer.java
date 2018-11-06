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
    private Object parseLiteral() {
        int startPosition = position;

        while (position < input.length()) {
            int currentPosition = position++;
            char key = input.charAt(currentPosition);

            if (key == ',' || key == ']' || key == '}') {
                String value = input.substring(startPosition, currentPosition - startPosition).trim();

                if (value.matches("^[+\-]?(?:0|[1-9]\d*)(?:[eE][+\-]?\d+)?$")) {
                    return Long.parseLong(value);
                } else if (value.matches("^[+\-]?(?:0|[1-9]\d*)(?:\.\d*)?(?:[eE][+\-]?\d+)?$")) {
                    return Double.parseDouble(value);
                } else if (value.matches("^(?i:(true|false|null))$")) {
                    return value.equalsIgnoreCase("null") ? null : Boolean.parseBoolean(value);
                }
            }
        }

        throw new JSONException("Malformed literal.");
    }

    /**
     *
     *
     * @param quoteType
     * @return
     */
    private String parseString(char quoteType) {
        StringBuilder output = new StringBuilder();
        boolean escapeString = false;

        while (position < input.length()) {
            char key = input.charAt(position++);

            if (escapeString) {
                escapeString = false;

                switch (key) {
                    case 'r': key = '\r'; break;
                    case 'n': key = '\n'; break;
                    case 't': key = '\t'; break;
                    case 'b': key = '\b'; break;
                    case 'f': key = '\f'; break;
                    // TODO: Handle unicode characters
                    // case 'u': ...

                    case '"':
                    case '\'':
                    case '\\':
                    case '/':
                        break;

                    default:
                        throw new JSONException("Unexpected symbol");
                }
            } else if (key == '\\')
                escapeString = true;
                continue;
            } else if (key == quoteType) {
                return output.toString();
            }

            output.append(key);
        }

        throw new JSONException("Unable to parse string.");
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
                    return parseString((char) token);
            }

            return parseLiteral();
        }

        throw new JSONException("End of file encountered while parsing.");
    }

    /**
     *
     *
     * @param input
     */
    public JSONTokenizer(String input) {
        this.input = input;
        position = 0;
    }
}