package fi.tamk.tiko.ohjelmointi.json;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 *
 * @author  Joonas Lauhala {@literal <joonas.lauhala@cs.tamk.fi>}
 * @version 2018.1101
 * @since   11
 */
public class JSONTokenizer implements Iterable<JSONType> {

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
    private Character expectedToken;

    /**
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     *
     */
    public void setExpectedToken(Character token) {
        expectedToken = token;
    }

    /**
     *
     */
    public boolean isExpectedToken(Character token) {
        return expectedToken != null && expectedToken.equals(token);
    }

    /**
     *
     *
     * @param skipToEOL
     * @return
     */
    private int skipVoidTokens(boolean skipToEOL) {
        while (position < input.length()) {
            int character = input.charAt(position++);

            if (!skipToEOL) {
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
                                        throw new JSONException("Malformed identifier - <unterminated comment> at position: %d", position);
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
    private char parseUnicode() {
        final int UNICODE_SIZE = 4;
        int endIndex = position + UNICODE_SIZE;

        if (endIndex <= input.length()) {
            String value = input.substring(position, endIndex).toLowerCase();

            if (value.matches("^[\\da-f]+$")) {
                int result = 0;

                for (int i = 0; i < value.length(); i++) {
                    char key = value.charAt(i);
                    result *= 16;

                    if (key >= 'a' && key <= 'f') {
                        result += 10 + (key - 'a');
                    } else {
                        result += key - '0';
                    }

                    position++;
                }

                return (char) result;
            }
        }

        throw new JSONException("Malformed identifier - invalid <unicode sequence> at position: %d", position);
    }

    /**
     *
     *
     * @param literal
     * @return
     */
    private JSONType getValidatedLiteral(String literal) {
        if (literal.matches("^[+\\-]?(?:0|[1-9]\\d*)(?:[eE][+\\-]?\\d+)?$")) {
            return JSONType.createNumber(Long.parseLong(literal));
        } else if (literal.matches("^[+\\-]?(?:0|[1-9]\\d*)(?:\\.\\d*)?(?:[eE][+\\-]?\\d+)?$")) {
            return JSONType.createDecimal(Double.parseDouble(literal));
        } else if (literal.matches("^(?i:(true|false))$")) {
            return JSONType.createBoolean(Boolean.parseBoolean(literal));
        } else if (literal.equalsIgnoreCase("null")) {
            return JSONType.createNull();
        }

        throw new JSONException("Malformed identifier - invalid <literal> at position: %d", position);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseArray() {
        JSONTokenizer tokenizer = new JSONTokenizer(input, position);

        JSONArray array = new JSONArray();
        JSONType token = null;

        while ((token = tokenizer.parseNext()) != null) {
            array.add(token);
            tokenizer.setExpectedToken(',');
        }

        if (!tokenizer.isExpectedToken(']')) {
            throw new JSONException("Malformed identifier - invalid <array> at position: %d", position);
        }

        position += tokenizer.getPosition();
        return JSONType.createArray(array);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseObject() {
        JSONTokenizer tokenizer = new JSONTokenizer(input, position);

        JSONObject object = new JSONObject();
        JSONType token = null;
        String key = null;

        while ((token = tokenizer.parseNext()) != null) {
            if (key == null) {
                try {
                    key = token.getAsString();
                    tokenizer.setExpectedToken(':');

                    continue;
                } catch (ClassCastException e) {
                    throw new JSONException("Identifier mismatch - <key> missing at position: %d", position);
                }
            }

            object.put(key, token);
            tokenizer.setExpectedToken(',');
            key = null;
        }

        if (key != null) {
            throw new JSONException("Identifier mismatch - <value> missing at position: %d", position);
        } else if (!tokenizer.isExpectedToken('}')) {
            throw new JSONException("Malformed identifier - invalid <object> at position: %d", position);
        }

        position += tokenizer.getPosition();
        return JSONType.createObject(object);
    }

    /**
     *
     *
     * @return
     */
    private JSONType parseLiteral() {
        int startPosition = --position;
        String value = null;

        do {
            if (position < input.length()) {
                char key = input.charAt(position);

                if (key == ',' || key == ']' || key == '}') {
                    value = input.substring(startPosition, position);
                } else {
                    position++;
                }
            } else {
                value = input.substring(startPosition);
            }
        } while (value == null);

        return getValidatedLiteral(value.trim());
    }

    /**
     *
     *
     * @param quoteType
     * @return
     */
    private JSONType parseString(char quoteType) {
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
                    case 'u': key = parseUnicode(); break;

                    case '"':
                    case '\'':
                    case '\\':
                    case '/':
                        break;

                    default:
                        throw new JSONException("Malformed identifier - <unexpected symbol> at position: %d", position);
                }
            } else if (key == '\\') {
                escapeString = true;
                continue;
            } else if (key == quoteType) {
                return JSONType.createString(output.toString());
            }

            output.append(key);
        }

        throw new JSONException("Malformed identifier - invalid <string> at position: %d", position);
    }

    /**
     *
     *
     * @return
     */
    private JSONType skipNext() {
        int storedPosition = position;
        JSONType token = parseNext();
        position = storedPosition;

        return token;
    }

    /**
     *
     *
     * @return
     */
    public JSONType parseNext() {
        int token = skipVoidTokens(false);

        if (expectedToken == null || !isExpectedToken((char) token)) {
	        switch (token) {
                case '[':
                    return parseArray();

                case '{':
                    return parseObject();

                case '"':
                case '\'':
                    return parseString((char) token);

                case -1:
                case ']':
                case '}':
                    if (token != -1) {
                        setExpectedToken((char) token);
                    }
                    return null;
            }

            return parseLiteral();
        }

        setExpectedToken(null);
        JSONType nextToken = parseNext();

        if (nextToken == null) {
            throw new JSONException("Identifier mismatch - <value> missing at position: %d", position);
        }

        return nextToken;
    }

    /**
     *
     *
     * @param stream
     */
    private JSONTokenizer(String stream, int position) {
        setExpectedToken(null);

        this.position = position;
        this.input = stream;
    }

    /**
     *
     *
     * @param stream
     */
    public JSONTokenizer(String stream) {
        this(stream, 0);
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeNumber(Long value) {
        return String.valueOf(value);
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeDecimal(Double value) {
        return String.valueOf(value);
    }

    /**
     *
     *
     * @param value
     * @param quoteType
     * @return
     */
    private static String writeString(String value, String quoteType) {
        StringBuilder output = new StringBuilder(quoteType);

        for (int i = 0; i < value.length(); i++) {
            String key = value.substring(i, i + 1);

            if (!key.equals(quoteType)) {
                switch (key) {
                    case "\t": key = "\\t"; break;
                    case "\r": key = "\\r"; break;
                    case "\n": key = "\\n"; break;
                    case "\f": key = "\\f"; break;
                    case "\b": key = "\\b"; break;
                    case "\"": key = "\\\""; break;
                    case "\\": key = "\\\\"; break;
                }
            } else {
                output.append("\\");
            }

            output.append(key);
        }

        return output.append(quoteType).toString();
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeString(String value) {
        return writeString(value, "\"");
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeSingleQuoteString(String value) {
        return writeString(value, "'");
    }

    /**
     *
     *
     * @param object
     * @return
     */
    public static String writeObject(JSONObject object) {
        AtomicBoolean hasPreviousItem = new AtomicBoolean();
        StringBuilder output = new StringBuilder("{");

        object.forEach((key, value) -> {
            if (hasPreviousItem.get()) {
                output.append(", ");
            }

            output.append(String.format("%s: %s", writeString(key), value));
            hasPreviousItem.set(true);
        });

        return output.append("}").toString();
    }

    /**
     *
     *
     * @param array
     * @return
     */
    public static String writeArray(JSONArray array) {
        AtomicBoolean hasPreviousItem = new AtomicBoolean();
        StringBuilder output = new StringBuilder("[");

        array.stream().forEach(value -> {
            if (hasPreviousItem.get()) {
                output.append(", ");
            }

            output.append(value);
            hasPreviousItem.set(true);
        });

        return output.append("]").toString();
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeBoolean(Boolean value) {
        return value.toString();
    }

    /**
     *
     *
     * @param value
     * @return
     */
    public static String writeNull() {
        return "null";
    }

    /**
     *
     */
    private class JSONTokenIterator implements Iterator<JSONType> {

        /**
         *
         *
         * @return
         */
        @Override
        public boolean hasNext() {
            return skipNext() != null;
        }

        /**
         *
         *
         * @return
         */
        @Override
        public JSONType next() {
            return parseNext();
        }
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Iterator<JSONType> iterator() {
        return new JSONTokenIterator();
    }
}